package transformer.impl;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.ToPostgreSQLTypeConverter;

import static com.mongodb.client.model.Filters.eq;
import static data.provider.FormatDataProvider.*;
import static data.provider.MongoDBStringConstantsProvider.*;

import converter.types.FieldDTOPostgreSQLTypes;
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
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    
    private DatabaseDTO databaseDTO;
    private Database to;
    private Database from;
    private final Map<String, Long> u_id = new HashMap<>();
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
//        if (!(to instanceof PostgreSQL) || (from instanceof PostgreSQL)) return;
        
        databaseDTO = from.makeDTO();
        this.from = from;
        this.to = to;
        
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
//            ToPostgreSQLTypeConverter.convertAllFields(databaseDTO);
            fillCollectionsTable();
            //can do more threads here (for current table)
            databaseDTO.getProvider().getDatabaseMetadata().forEach((tableData, fields) -> {
                try {
                    MongoClient mongoClient = ((MongoDB) from).getMongoClient();
                    long numOfDelimiters = tableData.getOldName().chars().filter(c -> c == '_').count();
                    if (isCollectionName(tableData.getOldName())) {
                        // that means - tableDTO is document (not sub object)
                        fillTableDataWithCollection(tableData.getOldName(), tableData.getTableDTO().getName(), fields, mongoClient);
    
                    } else if (numOfDelimiters <= 2 & !tableData.getOldName().equals(collectionTableName) ) {
                        System.out.println("ELSEIF");
                        System.out.println("TRUE");
                        callablesFillTableDataTasks.add(
                              new FillTableDataTask(tableData.getOldName(), tableData.getTableDTO().getName(), fields, mongoClient));
                    }
                }
                catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            });
        }
        
        executor.invokeAll(callablesFillTableDataTasks);
        callablesFillTableDataTasks.clear();
        
    }
    
    private void fillTableDataWithCollection(String collectionName, String tableNameOfCollection, Map<String, FieldDTO> fields, MongoClient mongoClient) throws
                                                                                                                                                         SQLException,
                                                                                                                                                        IOException {
        MongoDatabase db = mongoClient.getDatabase(databaseDTO.getName());
        MongoCollection<Document> collection = db.getCollection(collectionName);
      
        ScriptRunner sr = new ScriptRunner(((PostgreSQL) to).getConnection(), false, true);
        String fileName = "src/main/temp_" + collectionName + ".sql";
     
        for (Document doc : collection.find()) {
            fillValues(doc, tableNameOfCollection, collectionName, fields);
        }
        executeSqlFile(fileName, sr);
    
    }
    
    private boolean isCollectionName(String name){
        List<String> names = from.getNames();
        return names.contains(name);
    }
    
    // from MongoDB
    private void fillTableData(String oldTableName, String newTableName, Map<String, FieldDTO> fields, MongoClient mongoClientFrom) throws SQLException,
                                                                                                                                           IOException {
        String collectionName;
        String documentId;
        List<String> values = new ArrayList<>();
    
        String[] parts = oldTableName.split(delimiter);
    
        collectionName = parts[0];
        documentId = parts[2];
        values.add(collectionName);
    
        MongoDatabase db = mongoClientFrom.getDatabase(databaseDTO.getName());
        MongoCollection<Document> collection = db.getCollection(collectionName);
    
        Document doc = collection.find(eq("_id", new ObjectId(documentId))).first();
        if (doc != null) {
            String oldName = generateDocumentName(doc, collectionName);
            fillValues(doc, newTableName, oldName, fields);
            ScriptRunner sr = new ScriptRunner(((PostgreSQL) to).getConnection(), false, true);
            executeSqlFile(newTableName, sr);
        }
    }
    
    private void fillValues(Document doc, String newTableName, String oldTableName, Map<String, FieldDTO> fields) throws IOException, SQLException {
        String fileName = "src/main/temp_" + newTableName + ".sql";
        List<String> values = new ArrayList<>();
        System.out.println(fileName);
        File file = new File(fileName);
        clearFile(file);
        for (String key : doc.keySet()) {
            Object field = doc.get(key);
            
            //if it's object
            if (field instanceof DBObject) {
                String subObjectName = oldTableName + delimiter + key;
                long id = getUniqueId(subObjectName);
                values.add(String.valueOf(id));
                fillSubObjectTableData((DBObject) field, subObjectName, id);
                //if not object
            } else {
                values.add(doc.get(key).toString());
            }
        }
        String insertOneRowQuery =
              "INSERT INTO " + newTableName + " (" + getListOfNewFieldsNames(fields) + ") VALUES (" + getListOfValues(values) + ");\n";
        fillSqlFile(insertOneRowQuery, fileName);
    }
    
    private void clearFile(File file) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();
    }
    
    public void fillSqlFile(String query, String filename) throws IOException {
        Files.write(Paths.get(filename), query.getBytes(), StandardOpenOption.APPEND);
    }
    
    private void executeSqlFile(String fileName, ScriptRunner sr) throws IOException, SQLException {
        Reader reader = new BufferedReader(new FileReader(fileName));
        sr.runScript(reader);
    }
    
    private void fillCollectionsTable() throws SQLException {
    
        List<String> names = from.getNames();
        String collectionTableNewName = null;
        String collectionNewFieldName = null;
        
        for (TableData tableData : databaseDTO.getProvider().getDatabaseMetadata().keySet()) {
            if (tableData.getOldName().equals(collectionTableName)){
                collectionTableNewName = tableData.getTableDTO().getName();
                for (String oldName : databaseDTO.getProvider().getDatabaseMetadata().get(tableData).keySet()) {
                    if (oldName.equals(collectionFieldName)){
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
                System.out.println(insertQuery);
                statementTo.execute(insertQuery);
            }
            
        }
    }
    
    private void fillSubObjectTableData(DBObject ob, String subObjectName, long idInParentTable) throws SQLException {
        
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
            if (field instanceof DBObject) {
                String relTableName = subObjectName + key;
                long id = getUniqueId(relTableName);
                values.add(String.valueOf(id));
                fillSubObjectTableData((DBObject) field, relTableName, id);
                //if not object
            }
            else {
                values.add((String) ob.get(key));
            }
            
            Connection connectionTo = ((PostgreSQL) to).getConnection();
            String insertOneRowQuery =
                  "INSERT INTO " + newTableName + "(" + getListOfNewFieldsNames(fields) + ") VALUES (" + getListOfValues(values) + ")";
            Statement statementTo = connectionTo.createStatement();
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
        return "create table if not exists " +
               table.getName() +
               " ( " +
               generateSQLFields(table.getFields()) +
               "); ";
    }
    
    public static class GenerateSQLCreateTableTask
          implements Callable<String> {
        TableDTO tableDTO;
        Connection connection;
        
        public GenerateSQLCreateTableTask(TableDTO tableDTO, Connection connection) {
            this.tableDTO = tableDTO;
            this.connection = connection;
        }
        
        @Override
        public String call() throws SQLException {
            ToPostgreSQLTransformer transformer = new ToPostgreSQLTransformer();
            connection.createStatement().executeQuery(transformer.generateSQLCreateTable(tableDTO));
            return null;
        }
    }
    
    public static class GenerateSQLForeignKeysTask
          implements Callable<String> {
        TableDTO tableDTO;
        Connection connection;
        
        public GenerateSQLForeignKeysTask(TableDTO tableDTO, Connection connection) {
            this.tableDTO = tableDTO;
            this.connection = connection;
        }
        
        @Override
        public String call() throws SQLException {
            ToPostgreSQLTransformer transformer = new ToPostgreSQLTransformer();
            connection.createStatement().executeQuery(transformer.generateSQLForeignKeys(tableDTO));
            return null;
        }
    }
    
    public static class FillTableDataTask
          implements Callable<String> {
        String oldTableName;
        String newTableName;
        Map<String, FieldDTO> fields;
        MongoClient mongoClientFrom;
        
        public FillTableDataTask(String oldTableName, String newTableName, Map<String, FieldDTO> fields, MongoClient mongoClientFrom) {
            this.oldTableName = oldTableName;
            this.newTableName = newTableName;
            this.fields = fields;
            this.mongoClientFrom = mongoClientFrom;
        }
        
        
        @Override
        public String call() throws SQLException, IOException {
            ToPostgreSQLTransformer transformer = new ToPostgreSQLTransformer();
            transformer.fillTableData(oldTableName, newTableName, fields, mongoClientFrom);
            return null;
        }
    }
}

