package transformer.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.ToPostgreSQLTypeConverter;
import static com.mongodb.client.model.Filters.eq;
import static data.provider.FormatDataProvider.*;
import static data.provider.MongoDBStringConstantsProvider.*;
import converter.types.FieldDTOPostgreSQLTypes;
import data.NameType;
import data.SubObjectData;
import data.TableData;
import database.Database;
import database.MongoDB;
import database.PostgreSQL;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import org.bson.Document;
import org.bson.types.ObjectId;
import transformer.DBTransformer;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class ToPostgreSQLTransformer
      implements DBTransformer {

    private DatabaseDTO databaseDTO;
    private Database to;
    private Database from;
    private final Map<String, Long> u_id = new HashMap<>();
    private List<Map<NameType, List<String>>> objectNames;

    @Override
    public void transform(Database from, Database to) throws SQLException {
        if (!(to instanceof PostgreSQL) || (from instanceof PostgreSQL)) return;

        databaseDTO = from.makeDTO();
        this.from = from;
        this.to = to;
        objectNames = ((MongoDB) from).getObjectNames();

        try {
            createTables(databaseDTO, to);
            fillAllData();
            createFK(databaseDTO, to);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fillAllData() throws InterruptedException, SQLException {
        LinkedBlockingQueue<Callable<String>> callablesFillTableDataTasks = new LinkedBlockingQueue<>();
        if (databaseDTO.getMarker() == MongoDB.class) {
            ToPostgreSQLTypeConverter.convertAllFields(databaseDTO);
            fillCollectionsTable();
            //can do more threads here (for current table)

            databaseDTO.getProvider().getDatabaseMetadata().forEach((tableData, fields) -> {
                try {

                    MongoClient mongoClient = ((MongoDB) from).getMongoClient();
                    boolean isDocument = false;

                    Map<NameType, List<String>> currentMapName = new HashMap<>();

                    for (Map<NameType, List<String>> map : objectNames) {
                        if (map.containsKey(NameType.DOCUMENT) && getNameFromMap(map).equals(tableData.getOldName())){
                            isDocument = true;
                            currentMapName = map;
                            break;
                        } else if (map.containsKey(NameType.COLLECTION) && !map.containsKey(NameType.DOCUMENT) && getNameFromMap(map).equals(tableData.getOldName())){
                            currentMapName = map;
                            break;
                        }
                    }

                    if (isCollectionName(tableData.getOldName())) {
                        fillTableDataWithCollection(currentMapName, tableData.getOldName(), tableData.getTableDTO().getName(), fields,
                                                    mongoClient);

                    } else if (isDocument) {
                        callablesFillTableDataTasks.add(
                              new FillTableDataTask(currentMapName, tableData.getTableDTO().getName(),
                                                    fields, mongoClient));
                    }
                }
                catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            });
        }

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        executor.invokeAll(callablesFillTableDataTasks);
        callablesFillTableDataTasks.clear();

    }

    private boolean isCollectionName(String name) {
        List<String> names = from.getNames();
        return names.contains(name);
    }

    private void fillTableDataWithCollection(Map<NameType, List<String>> currentMapName,
                                             String collectionName,
                                             String tableNameOfCollection,
                                             Map<String, FieldDTO> fields,
                                             MongoClient mongoClient) throws
                                                                      SQLException,
                                                                      IOException {
        MongoDatabase db = mongoClient.getDatabase(databaseDTO.getName());
        MongoCollection<Document> collection = db.getCollection(collectionName);

        for (Document doc : collection.find()) {
            fillValues(NameType.COLLECTION, currentMapName, doc, tableNameOfCollection, fields, null);
        }
//        String fileName = "src/main/temp_" + collectionName + ".sql";
//        executeSqlFile(fileName);

    }

    private void fillTableData(Map<NameType, List<String>> currentMapName, String newTableName, Map<String, FieldDTO> fields,
                               MongoClient mongoClientFrom) throws SQLException,
                                                                                                                                                             IOException {
        String documentId = currentMapName.get(NameType.ID).get(0);

        MongoDatabase db = mongoClientFrom.getDatabase(databaseDTO.getName());
        MongoCollection<Document> collection = db.getCollection(currentMapName.get(NameType.COLLECTION).get(0));

        Document doc = collection.find(eq("_id", new ObjectId(documentId))).first();
        if (doc != null) {
            File file = new File("src/main/temp_" + newTableName + ".sql");
//            clearFile(file);
            fillValues(NameType.DOCUMENT, currentMapName, doc, newTableName, fields, null);
//            executeSqlFile(newTableName);
//            file.delete();
        }
    }

    private void fillValues(NameType type, Map<NameType, List<String>> currentMapName, Document doc, String newTableName,
                            Map<String, FieldDTO> fields, Map<String, String> subValues) throws IOException, SQLException {

        Map<String, String> values = new HashMap<>();

        if (type == NameType.DOCUMENT){
            String collectionName = currentMapName.get(NameType.COLLECTION).get(0);
            values.put(fields.get(collectionFieldName).getName(), collectionName);
        } else if (type == NameType.SUB_OBJECT){
            values.putAll(subValues);
        }

        String oldTableName = getNameFromMap(currentMapName);
        List<SubObjectData> subObjects = new ArrayList<>();

        String fileName = "src/main/temp_" + newTableName + ".sql";
        for (String key : doc.keySet()) {
            Object field = doc.get(key);
            //if it's object
            if (field instanceof Document) {
                Map<String, String> subObjectValues = new HashMap<>();
                Map<NameType, List<String>> subMapName = new HashMap<>();
                Document subField = null;
                String newSubObjectTableName = null;
                Map<String, FieldDTO> subObjectFields = new HashMap<>();

                String subObjectName = oldTableName + delimiterForNames + key;

                for (TableData tableData : databaseDTO.getProvider().getDatabaseMetadata().keySet()) {
                    if (tableData.getOldName().equals(subObjectName)){
                        subObjectFields = databaseDTO.getProvider().getDatabaseMetadata().get(tableData);
                        newSubObjectTableName = tableData.getTableDTO().getName();
                    }
                }

                long id = getUniqueId(subObjectName);
                values.put(key, String.valueOf(id));
                if (newSubObjectTableName != null) {
                    subField = (Document) field;
                    subObjectValues.put(documentIdFieldName, String.valueOf(id));
                    for (Map<NameType, List<String>> map : objectNames) {
                        if (map.containsKey(NameType.SUB_OBJECT) && map.get(NameType.SUB_OBJECT).get(0).equals(subObjectName)){
                            subMapName = map;
                        }
                    }
                }
                subObjects.add(new SubObjectData(subObjectValues, subMapName, subField, newSubObjectTableName, subObjectFields));

            } else {
                values.put(fields.get(key).getName(), doc.get(key).toString());
            }
        }

        List<String> fieldsValues = new ArrayList<>();
        values.keySet().forEach(k -> fieldsValues.add(values.get(k)));

        List<String> fieldsNames = new ArrayList<>(values.keySet());

        Connection connectionTo = ((PostgreSQL) to).getConnection();
        Statement statementTo = connectionTo.createStatement();
        String insertOneRowQuery =
              "INSERT INTO " + newTableName + "(" + getListOfFields(fieldsNames) + ") VALUES (" + getListOfValues(fieldsValues) + ");";
        System.out.println("fillValues: " + insertOneRowQuery);
        statementTo.execute(insertOneRowQuery);
//        fillSqlFile(insertOneRowQuery, fileName);
        for (SubObjectData subObject : subObjects) {
            fillValues(NameType.SUB_OBJECT, subObject.getMapName(), subObject.getField(), subObject.getNewTableName(), subObject.getFields(), subObject.getValues());
        }
    }

    private void fillCollectionsTable() throws SQLException {

        List<String> names = from.getNames();
        String collectionTableNewName = null;
        String collectionNewFieldName = null;

        for (TableData tableData : databaseDTO.getProvider().getDatabaseMetadata().keySet()) {
            if (tableData.getOldName().equals(collectionTableName)) {
                collectionTableNewName = tableData.getTableDTO().getName();
                for (String oldName : databaseDTO.getProvider().getDatabaseMetadata().get(tableData).keySet()) {
                    if (oldName.equals(collectionFieldName)) {
                        collectionNewFieldName = databaseDTO.getProvider().getDatabaseMetadata().get(tableData).get(oldName).getName();
                        break;
                    }
                }
                break;
            }
        }

        if (collectionNewFieldName != null & collectionTableNewName != null) {
            Connection connectionTo = ((PostgreSQL) to).getConnection();
            Statement statementTo = connectionTo.createStatement();
            for (String name : names) {
                String insertQuery =
                      "INSERT INTO " + collectionTableNewName + " (" + collectionNewFieldName + ") VALUES ('" + name + "')";
                System.out.println("fillCollectionsTable: " + insertQuery);
                statementTo.execute(insertQuery);
            }

        }
    }

    private long getUniqueId(String tableName) {
        for (String name : u_id.keySet()) {
            if (name.equals(tableName)) {

                long id = u_id.get(name);
                u_id.put(name, ++id);
                return id;
            }
        }
        long id = 0;
        u_id.put(tableName, id);
        return id;
    }

    private void createFK(DatabaseDTO databaseDTO, Database to) throws InterruptedException, SQLException {
        LinkedBlockingQueue<Callable<String>> callablesAddForeignKeysTasks = new LinkedBlockingQueue<>();
        for (TableDTO table : databaseDTO.getTables()) {
            callablesAddForeignKeysTasks.add(new GenerateSQLForeignKeysTask(table, ((PostgreSQL) to).getConnection()));
        }
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        executor.invokeAll(callablesAddForeignKeysTasks);
        callablesAddForeignKeysTasks.clear();
    }

    private void createTables(DatabaseDTO databaseDTO, Database to) throws SQLException, InterruptedException {
        if (databaseDTO.getMarker() == MongoDB.class) {
            ToPostgreSQLTypeConverter.convertAllFields(databaseDTO);
        }
        LinkedBlockingQueue<Callable<String>> callablesCreateTableTasks = new LinkedBlockingQueue<>();
        for (TableDTO table : databaseDTO.getTables()) {
            callablesCreateTableTasks.add(new GenerateSQLCreateTableTask(table, ((PostgreSQL) to).getConnection()));
        }

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        executor.invokeAll(callablesCreateTableTasks);
        callablesCreateTableTasks.clear();
    }

    private String generateSQLFields(List<FieldDTO> fields) {
        StringBuilder fieldsString = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            fieldsString
                  .append(fields.get(i).getName())
                  .append(" ")
                  .append(((FieldDTOPostgreSQLTypes) fields.get(i).getType()).getType())
                  .append(fields.get(i).isPK() ? " primary key" : "");
            if (i != fields.size() - 1) {
                fieldsString.append(", ");
            }
        }
        return fieldsString.toString();
    }

    public String generateSQLForeignKeys(TableDTO table) {
        StringBuilder addForeignKeysSQL = new StringBuilder();
        for (FieldDTO fields : table.getFields()) {
            if (fields.getFK() != null) {
                String fkName = "fk_" + table.getName() + "_" + fields.getFK().getRelTableName();
                addForeignKeysSQL
                      .append("alter table ")
                      .append(table.getName())
                      .append(" drop constraint if exists ")
                      .append(fkName)
                      .append(";")
                      .append(" alter table ")
                      .append(table.getName())
                      .append(" add constraint ")
                      .append(fkName)
                      .append(" foreign key (")
                      .append(fields.getName())
                      .append(") ")
                      .append("references ")
                      .append(fields.getFK().getRelTableName())
                      .append(" (")
                      .append(fields.getFK().getRelFieldName())
                      .append("); ");
            }
        }
        return addForeignKeysSQL.toString();
    }

    public String generateSQLCreateTable(TableDTO table) {
        return "create table if not exists " + "\"" +
               table.getName() + "\"" +
               " ( " +
               generateSQLFields(table.getFields()) +
               "); ";
    }

    public class GenerateSQLCreateTableTask
          implements Callable<String> {
        TableDTO tableDTO;
        Connection connection;

        public GenerateSQLCreateTableTask(TableDTO tableDTO, Connection connection) {
            this.tableDTO = tableDTO;
            this.connection = connection;
        }

        @Override
        public String call() throws SQLException {
            connection.createStatement().executeQuery(generateSQLCreateTable(tableDTO));
            return null;
        }
    }

    public class GenerateSQLForeignKeysTask
          implements Callable<String> {
        TableDTO tableDTO;
        Connection connection;

        public GenerateSQLForeignKeysTask(TableDTO tableDTO, Connection connection) {
            this.tableDTO = tableDTO;
            this.connection = connection;
        }

        @Override
        public String call() throws SQLException {
            connection.createStatement().executeQuery(generateSQLForeignKeys(tableDTO));
            return null;
        }
    }

    public class FillTableDataTask
          implements Callable<String> {
        String newTableName;
        Map<String, FieldDTO> fields;
        MongoClient mongoClientFrom;
        Map<NameType, List<String>> currentName;

        public FillTableDataTask(Map<NameType, List<String>> currentName, String newTableName,
                                 Map<String, FieldDTO> fields,
                                 MongoClient mongoClientFrom) {
            this.newTableName = newTableName;
            this.fields = fields;
            this.mongoClientFrom = mongoClientFrom;
            this.currentName = currentName;
        }

        @Override
        public String call() throws SQLException, IOException {
            fillTableData(currentName, newTableName, fields, mongoClientFrom);
            return null;
        }
    }
}

