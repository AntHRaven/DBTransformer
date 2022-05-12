package transformer.impl;

import com.ibatis.common.jdbc.ScriptRunner;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.ToPostgreSQLTypeConverter;
import static com.mongodb.client.model.Filters.eq;
import static transformer.FormatDataProvider.*;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class ToPostgreSQLTransformer implements DBTransformer {
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    
    private DatabaseDTO databaseDTO;
    private Connection connectionTo;
    private Database from;
    private final Map<String, Long> u_id = new HashMap<>();
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        if (!(to instanceof PostgreSQL)) return;
        
        connectionTo = ((PostgreSQL) to).getConnection();
        databaseDTO = from.makeDTO();
        this.from = from;
        try {
            createTables(databaseDTO, to);
            fillAllData();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    
    private void fillAllData(){
        //can do more threads here (for current table)
        String collectionTableOldName = "collections";
        databaseDTO.getProvider().getDatabaseMetadata().forEach((tableData, fields) -> {
            try {
                if (databaseDTO.getMarker() == MongoDB.class) {
                    MongoClient mongoClient = ((MongoDB) from).getMongoClient();
                    ToPostgreSQLTypeConverter.convertAllFields(databaseDTO);
                    fillCollectionsTable();
                    long numOfDelimiters = tableData.getOldName().chars().filter(c -> c == '_').count();
                    if (numOfDelimiters <= 2 & !tableData.getOldName().equals(collectionTableOldName) ) {
                        // that means - tableDTO is document (not sub object)
                        fillTableData(tableData.getOldName(), tableData.getTableDTO().getName(), fields, mongoClient);
                    }
                } else if (databaseDTO.getMarker() == PostgreSQL.class){
                    fillTableData(tableData.getOldName(), tableData.getTableDTO().getName(), fields, ((PostgreSQL) from).getConnection());
                }
            } catch (SQLException | IOException e) {
                //something
            }
        });
    }
    
    // from PostgreSQL
    private void fillTableData(String oldTableName, String newTableName, Map<String, FieldDTO> fields, Connection connectionFrom) throws SQLException,
                                                                                                                                         IOException {
        Statement statementFrom = connectionFrom.createStatement();
        String selectQuery = "SELECT " + getListOfOldFieldsNames(fields) + " FROM " + oldTableName;
        ResultSet table = statementFrom.executeQuery(selectQuery);
    
        clearFile("src/main/temp.sql");
        
        while (table.next()){
            ArrayList<String> values = new ArrayList<>();
            for (String oldFieldName : fields.keySet()) {
                values.add(table.getString(oldFieldName));
            }
            String insertOneRowQuery =
                  "INSERT INTO " + newTableName + "(" + getListOfNewFieldsNames(fields) + ") VALUES (" + getListOfValues(values) + ")";
            Statement statementTo = connectionTo.createStatement();
            statementTo.executeQuery(insertOneRowQuery);
            fillSqlFile(insertOneRowQuery);
        }
        
        executeSqlFile();
    }
    
    private void clearFile(String path) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(path);
        writer.print("");
        writer.close();
    }
    
    public void fillSqlFile(String query) throws IOException {
        Files.write(Paths.get("src/main/temp.sql"), query.getBytes(), StandardOpenOption.APPEND);
    }
    
    private void executeSqlFile() throws IOException, SQLException {
        ScriptRunner sr = new ScriptRunner(connectionTo, false, true);
        Reader reader = new BufferedReader(new FileReader("src/main/temp.sql"));
        sr.runScript(reader);
    }
    
    private void fillCollectionsTable() throws SQLException {
        List<String> names = from.getNames();
        String collectionTableOldName = "collections";
        String collectionTableNewName = null;
        String collectionOldFieldName = "collection_name";
        String collectionNewFieldName = null;
    
        for (TableData tableData : databaseDTO.getProvider().getDatabaseMetadata().keySet()) {
            if (tableData.getOldName().equals(collectionTableOldName)){
                collectionTableNewName = tableData.getTableDTO().getName();
                for (String oldName : databaseDTO.getProvider().getDatabaseMetadata().get(tableData).keySet()) {
                    if (oldName.equals(collectionOldFieldName)){
                        collectionNewFieldName = databaseDTO.getProvider().getDatabaseMetadata().get(tableData).get(oldName).getName();
                        break;
                    }
                }
                break;
            }
        }
    
        if (collectionNewFieldName != null & collectionTableNewName != null) {
            Statement statementTo = connectionTo.createStatement();
            for (String name : names) {
                String insertQuery =
                      "INSERT INTO " + collectionTableNewName + "(" + collectionNewFieldName + ") VALUES (" + name + ")";
                statementTo.executeQuery(insertQuery);
            }
            
        }
    }
    
    // from MongoDB
    private void fillTableData(String oldTableName, String newTableName, Map<String, FieldDTO> fields, MongoClient mongoClientFrom) throws SQLException,
                                                                                                                                           IOException {
        
        String collectionName;
        String documentId;
        String delimiter = "_";
        
        List<String> values = new ArrayList<>();
        
        String[] parts = oldTableName.split(delimiter);
        
        collectionName = parts[0];
        documentId = parts[2];
        values.add(collectionName);
        
        MongoDatabase db = mongoClientFrom.getDatabase(databaseDTO.getName());
        MongoCollection<Document> collection = db.getCollection(collectionName);
        
        Document doc = collection.find(eq("_id", new ObjectId(documentId))).first();
        if (doc == null) {
            return;
        } else {
            String name = MongoDB.generateDocumentName(doc, collectionName);
            for (String key : doc.keySet()) {
                Object field = doc.get(key);
                
                //if it's object
                if (field instanceof DBObject) {
                    String subObjectName = name + delimiter + key;
                    long id = getUniqueId(subObjectName);
                    values.add(String.valueOf(id));
                    fillSubObjectTableData((DBObject) field, subObjectName, id);
                    //if not object
                } else {
                    values.add((String) doc.get(key));
                }
            }
            
            String insertOneRowQuery =
                  "INSERT INTO " + newTableName + "(" + getListOfNewFieldsNames(fields) + ") VALUES (" + getListOfValues(values) + ");\n";
            Statement statementTo = connectionTo.createStatement();
            statementTo.executeQuery(insertOneRowQuery);
        }
    }
    
    private void fillSubObjectTableData(DBObject ob, String subObjectName, long idInParentTable) throws SQLException {
    
        String newTableName = "";
        Map<String, FieldDTO> fields = new HashMap<>();
        
        for (TableData tableData : databaseDTO.getProvider().getDatabaseMetadata().keySet()) {
            if (tableData.getOldName().equals(subObjectName)){
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
            } else {
                values.add((String) ob.get(key));
            }
        
            String insertOneRowQuery =
                  "INSERT INTO " + newTableName + "(" + getListOfNewFieldsNames(fields) + ") VALUES (" + getListOfValues(values) + ")";
            Statement statementTo = connectionTo.createStatement();
            statementTo.executeQuery(insertOneRowQuery);
        }
    }
    
    private long getUniqueId(String tableName){
        for (String name : u_id.keySet()) {
            if (name.equals(tableName)){
                long id = u_id.get(name);
                u_id.put(name, ++id);
                return id;
            }
        }
        long id = 0;
        u_id.put(tableName, id);
        return id;
    }
    
    private void createTables(DatabaseDTO databaseDTO, Database to) throws SQLException, InterruptedException {
        LinkedBlockingQueue<Callable<String>> callablesCreateTableTasks = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Callable<String>> callablesAddForeignKeysTasks = new LinkedBlockingQueue<>();
        
        for (TableDTO table : databaseDTO.getTables()) {
            callablesCreateTableTasks.add(new GenerateSQLCreateTableTask(table, ((PostgreSQL) to).getConnection()));
            callablesAddForeignKeysTasks.add(new GenerateSQLForeignKeysTask(table, ((PostgreSQL) to).getConnection()));
        }
        
        executor.invokeAll(callablesCreateTableTasks);
        executor.invokeAll(callablesAddForeignKeysTasks);
        callablesAddForeignKeysTasks.clear();
        callablesCreateTableTasks.clear();
    }
    
    private String generateSQLFields(List<FieldDTO> fields) {
        StringBuilder fieldsString = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            fieldsString
                  .append(fields.get(i).getName())
                  .append(" ")
                  .append(fields.get(i).getType())
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
        System.out.println("create table if not exists " +
                           table.getName() +
                           " ( " +
                           generateSQLFields(table.getFields()) +
                           "); ");
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
    
    public static class GenerateSQLForeignKeysTask implements Callable<String> {
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
}

