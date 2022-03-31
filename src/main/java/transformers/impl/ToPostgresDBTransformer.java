package transformers.impl;

import dto.DataBaseDto;
import dto.ForeignKey;
import dto.TableDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import dto.FieldDto;
import readers.DBReader;
import transformers.DBTransformer;

public class ToPostgresDBTransformer
      implements DBTransformer {
    @Override
    public void transform(DBReader reader, Connection fromConnection, Connection toConnection) throws SQLException {
        List<TableDto> tables = reader.getAllTablesData(fromConnection);
        createTables(toConnection, tables);
    }
    
    @Override
    public void transform(DataBaseDto dataBase, Connection fromConnection, Connection toConnection) throws SQLException {
        List<TableDto> tables = dataBase.getTables();
        createTables(toConnection, tables);
    }
    
    private void createTables(Connection toConnection, List<TableDto> tables) throws SQLException {
        StringBuilder createAllTablesSQL = new StringBuilder();
        StringBuilder addAllForeignKeysSQL = new StringBuilder();
        
        for (TableDto table : tables) {
            if (table.getForeignKeys() != null) {
                addAllForeignKeysSQL.append(addForeignKeys(table));
            }
            createAllTablesSQL.append(createTable(table));
        }
        Statement statement = toConnection.createStatement();
        statement.executeUpdate(createAllTablesSQL.toString());
        statement.executeUpdate(addAllForeignKeysSQL.toString());
        statement.close();
    }
    
    private String convertFieldsToString(List<FieldDto> fields) {
        StringBuilder fieldsString = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            fieldsString.append(fields.get(i).toString());
            if (i != fields.size() - 1) {
                fieldsString.append(", ");
            }
        }
        
        return fieldsString.toString();
    }
    
    private String addForeignKeys(TableDto table) {
        StringBuilder addForeignKeysSQL = new StringBuilder();
        for (ForeignKey foreignKey : table.getForeignKeys()) {
            String fkName = "fk_" + table.getName() + "_" + foreignKey.getToTable();
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
                  .append(foreignKey.getFieldsFrom())
                  .append(") ")
                  .append("references ")
                  .append(foreignKey.getToTable())
                  .append(" (")
                  .append(foreignKey.getFieldsTo())
                  .append("); ");
        }
        return addForeignKeysSQL.toString();
    }
    
    private String createTable(TableDto table) {
        return "create table if not exists " +
               table.getName() +
               " ( " +
               convertFieldsToString(table.getFields()) +
               "); ";
    }
    
}
