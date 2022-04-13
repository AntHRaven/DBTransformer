package transformer.impl;

import database.Database;
import database.PostgreSQL;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import org.postgresql.ds.PGConnectionPoolDataSource;
import transformer.DBTransformer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.*;

public class ToPostgresDBTransformer
      implements DBTransformer {
    
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    
    
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
        StringBuilder createAllTablesSQL = new StringBuilder();
        StringBuilder addAllForeignKeysSQL = new StringBuilder();
        
        LinkedBlockingQueue<Callable<String>> callablesCreateTableTasks = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Callable<String>> callablesAddForeignKeysTasks = new LinkedBlockingQueue<>();
      
        Statement statement = ((PostgreSQL) to).getConnection().createStatement();
        Connection connection = ((PostgreSQL) to).getConnection();
        
        PGConnectionPoolDataSource source = (PGConnectionPoolDataSource) ((PostgreSQL) to).getConnection();
        
        for (TableDTO table : databaseDTO.getTables()) {
            callablesCreateTableTasks.add(new GenerateSQLCreateTable(table));
            callablesAddForeignKeysTasks.add(new GenerateSQLForeignKeys(table));
        }
        
        List<Future<String>> resultTableTasks = executor.invokeAll(callablesCreateTableTasks);
        List<Future<String>> resultForeignKeysTasks = executor.invokeAll(callablesAddForeignKeysTasks);
        
        resultTableTasks.forEach((item) -> {
            try {
                createAllTablesSQL.append(item.get());
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        
        resultForeignKeysTasks.forEach((item) -> {
            try {
                addAllForeignKeysSQL.append(item.get());
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        
        statement.executeUpdate(createAllTablesSQL.toString());
        statement.executeUpdate(addAllForeignKeysSQL.toString());
        
        callablesCreateTableTasks.clear();
        callablesAddForeignKeysTasks.clear();
        
        statement.close();
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
    
    public static class GenerateSQLCreateTable
          implements Callable<String> {
        TableDTO tableDTO;
        
        public GenerateSQLCreateTable(TableDTO tableDTO) {
            this.tableDTO = tableDTO;
        }
        
        @Override
        public String call() {
            ToPostgresDBTransformer toPostgresDBTransformer = new ToPostgresDBTransformer();
            return toPostgresDBTransformer.generateSQLCreateTable(tableDTO);
        }
    }
    
    public static class GenerateSQLForeignKeys
          implements Callable<String> {
        TableDTO tableDTO;
        
        public GenerateSQLForeignKeys(TableDTO tableDTO) {
            this.tableDTO = tableDTO;
        }
        
        @Override
        public String call() {
            ToPostgresDBTransformer toPostgresDBTransformer = new ToPostgresDBTransformer();
            return toPostgresDBTransformer.generateSQLForeignKeys(tableDTO);
        }
    }
    
    
}
