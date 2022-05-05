package transformer.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import database.Database;
import database.PostgreSQL;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import transformer.DBTransformer;

import java.util.concurrent.*;

public class ToPostgreSQLDBTransformer
      implements DBTransformer {
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private DatabaseDTO databaseDTO;
    private Connection connection;
    
    public void fillAllData(){
        //can do more threads here (for current table)
        databaseDTO.provider.getDatabaseMetadata().forEach((tableData, fields) ->
            {
                try {
                    fillTableData(tableData.getOldName(), tableData.getTableDTO().getName(), fields);
                } catch (SQLException e) {
                    //nothing?
                }
            }
        );
    }
    
    public void fillTableData(String oldTableName, String newTableName, Map<String, FieldDTO> fields) throws SQLException {
        Statement statement = connection.createStatement();
        String selectQuery = "SELECT " + getListOfOldFieldsNames(fields) + " FROM " + oldTableName;
        ResultSet table = statement.executeQuery(selectQuery);
        
        while (table.next()){
            ArrayList<String> values = new ArrayList<>();
            for (String oldFieldName : fields.keySet()) {
                values.add(table.getString(oldFieldName));
            }
            String insertOneRowQuery =
                  "INSERT INTO " + newTableName + "(" + getListOfNewFieldsNames(fields) + ") VALUES (" + getListOfValues(values) + ")";
            statement.executeQuery(insertOneRowQuery);
        }
    }
    
    private String getListOfOldFieldsNames(Map<String, FieldDTO> fields){
        StringBuilder list = new StringBuilder();
        for (String key : fields.keySet()) {
            list.append(key).append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    private String getListOfNewFieldsNames(Map<String, FieldDTO> fields){
        StringBuilder list = new StringBuilder();
        for (String key : fields.keySet()) {
            list.append(fields.get(key).getName()).append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    private String getListOfValues(ArrayList<String> values){
        StringBuilder list = new StringBuilder();
        for (String val : values) {
            list.append("'").append(val).append("'").append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        try {
            createTables(from.makeDTO(), to);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void transform(DatabaseDTO from, Database to) throws SQLException {
        try {
            createTables(from, to);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        executor.shutdown();
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
    
    private String generateSQLForeignKeys(TableDTO table) {
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
    
    private String generateSQLCreateTable(TableDTO table) {
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
        public String call() throws SQLException, InterruptedException {
            ToPostgreSQLDBTransformer toPostgresDBTransformer = new ToPostgreSQLDBTransformer();
            System.out.println(toPostgresDBTransformer.generateSQLCreateTable(tableDTO));
            connection.createStatement().executeQuery(toPostgresDBTransformer.generateSQLCreateTable(tableDTO));
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
            ToPostgreSQLDBTransformer toPostgresDBTransformer = new ToPostgreSQLDBTransformer();
            connection.createStatement().executeQuery(toPostgresDBTransformer.generateSQLForeignKeys(tableDTO));
            return null;
        }
    }
    
    
}
