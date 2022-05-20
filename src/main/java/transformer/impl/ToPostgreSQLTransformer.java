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
import com.ibatis.common.jdbc.ScriptRunner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        executor.shutdown();
        
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
            Map<String, String> values = new HashMap<>();
            fillValues(NameType.COLLECTION, currentMapName, doc, tableNameOfCollection, fields, values);
        }
        String fileName = "src/main/temp_" + collectionName + ".sql";
        executeSqlFile(fileName);
        
    }
    
    private void fillTableData(Map<NameType, List<String>> currentMapName, String newTableName, Map<String, FieldDTO> fields,
                               MongoClient mongoClientFrom) throws SQLException,
                                                                                                                                                             IOException {
        String collectionName = currentMapName.get(NameType.COLLECTION).get(0);
        String documentId = currentMapName.get(NameType.ID).get(0);
        Map<String, String> values = new HashMap<>();
        
        values.put(collectionName, fields.get(collectionFieldName).getName());
        
        MongoDatabase db = mongoClientFrom.getDatabase(databaseDTO.getName());
        MongoCollection<Document> collection = db.getCollection(collectionName);
        
        Document doc = collection.find(eq("_id", new ObjectId(documentId))).first();
        if (doc != null) {
            File file = new File("src/main/temp_" + newTableName + ".sql");
            clearFile(file);
            fillValues(NameType.DOCUMENT, currentMapName, doc, newTableName, fields, values);
            executeSqlFile(newTableName);
            file.delete();
        }
    }
    
    private void fillValues(NameType type, Map<NameType, List<String>> currentMapName, Document doc, String newTableName,
                            Map<String, FieldDTO> fields, Map<String, String> values) throws IOException, SQLException {
        
        String oldTableName = getNameFromMap(currentMapName);
    
        System.out.println("OldTableName: " + oldTableName);
        
        String fileName = "src/main/temp_" + newTableName + ".sql";
        for (String key : doc.keySet()) {
            Object field = doc.get(key);
            //if it's object
            if (field instanceof Document) {
    
                String subObjectName = oldTableName + delimiterForNames + key;
                Map<String, FieldDTO> subObjectFields = new HashMap<>();
    
                for (TableData tableData : databaseDTO.getProvider().getDatabaseMetadata().keySet()) {
                    if (tableData.getOldName().equals(subObjectName)){
                        subObjectFields = databaseDTO.getProvider().getDatabaseMetadata().get(tableData);
                    }
                }
    
                System.out.println("Object: " + key + " - subObjectName = " + subObjectName);
                System.out.println("Relational: " + currentMapName.get(NameType.RELATION).get(0));
    
                System.out.println("================");
                for (String k : subObjectFields.keySet()) {
                    System.out.println(k);
                }
                System.out.println("================");
                
                long id = getUniqueId(subObjectName);
                values.put(String.valueOf(id), subObjectFields.get(key + documentIdFieldName).getName());
                System.out.println("---------------------");
                fillSubObjectTableData((Document) field, subObjectName, id);
                //if not object
            }
            else {
                System.out.println("Not object: " + key);
                values.put(doc.get(key).toString(), fields.get(key).getName());
                System.out.println("---------------------");
            }
        }
        
        List<String> fieldsNames = new ArrayList<>();
        values.keySet().forEach(k -> fieldsNames.add(values.get(k)));
        
        List<String> fieldsValues = new ArrayList<>(values.keySet());
        
        String insertOneRowQuery =
              "INSERT INTO " + newTableName + "(" + getListOfFields(fieldsNames) + ") VALUES (" + getListOfValues(fieldsValues) + "); \n";
        fillSqlFile(insertOneRowQuery, fileName);
    }
    
    private void clearFile(File file) throws IOException {
        
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();
    }
    
    public void fillSqlFile(String query, String filename) throws IOException {
//        System.out.println("QUERY: " + query);
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.write(Paths.get(filename), query.getBytes(), StandardOpenOption.APPEND);
    }
    
    private void executeSqlFile(String fileName) throws IOException, SQLException {
        Reader reader = new BufferedReader(new FileReader(fileName));
        ScriptRunner sr = new ScriptRunner(((PostgreSQL) to).getConnection(), false, true);
        sr.runScript(reader);
        File file = new File(fileName);
        file.delete();
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
                statementTo.execute(insertQuery);
            }
            
        }
    }
    
    private void fillSubObjectTableData(Document ob, String subObjectName, long idInParentTable) throws SQLException {
    
        System.out.println("Come to fillSubObjectTableData");
        
        String newTableName = "";
        Map<String, FieldDTO> fields = new HashMap<>();
        
        for (TableData tableData : databaseDTO.getProvider().getDatabaseMetadata().keySet()) {
            if (tableData.getOldName().equals(subObjectName)) {
                newTableName = tableData.getTableDTO().getName();
                fields = databaseDTO.getProvider().getDatabaseMetadata().get(tableData);
                break;
            }
        }
        
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(idInParentTable));
        
        for (String key : ob.keySet()) {
            Object field = ob.get(key);
            
            //if it's object
            if (field instanceof Document) {
                String relTableName = subObjectName + key;
                long id = getUniqueId(relTableName);
                values.add(String.valueOf(id));
                fillSubObjectTableData((Document) field, relTableName, id);
                //if not object
            }
            else {
                values.add((String) ob.get(key));
            }
            
            Connection connectionTo = ((PostgreSQL) to).getConnection();
            String insertOneRowQuery =
                  "INSERT INTO " + newTableName + "(" + getListOfNewFieldsNames(fields) + ") VALUES (" + getListOfValues(values) + ")";
            Statement statementTo = connectionTo.createStatement();
            System.out.println(insertOneRowQuery);
            statementTo.executeQuery(insertOneRowQuery);
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
        executor.shutdown();
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
        executor.shutdown();
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

