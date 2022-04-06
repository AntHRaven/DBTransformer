package transformer.impl;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import database.Database;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import transformer.DBTransformer;

public class ToPostgresDBTransformer
      implements DBTransformer {
    
    private DatabaseDTO databaseDTO;
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        createTables(from.makeDTO(), to);
    }
    
    private void createTables(DatabaseDTO databaseDTO, Database to) throws SQLException {
        StringBuilder createAllTablesSQL = new StringBuilder();
        StringBuilder addAllForeignKeysSQL = new StringBuilder();
        
        for (TableDTO table : databaseDTO.getTables()) {
            createAllTablesSQL.append(generateSQLCreateTable(table));
            addAllForeignKeysSQL.append(generateSQLForeignKeys(table));
        }
        Statement statement = to.getConnection().createStatement();
        statement.executeUpdate(createAllTablesSQL.toString());
        statement.executeUpdate(addAllForeignKeysSQL.toString());
        statement.close();
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
    
}
