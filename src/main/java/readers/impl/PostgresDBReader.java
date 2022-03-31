package readers.impl;

import dto.DataBaseDto;
import dto.FieldDto;
import dto.ForeignKey;
import dto.TableDto;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import readers.DBReader;
import utils.fieldTypes.PostgresTypes;

public class PostgresDBReader
      implements DBReader {
    
    @Override
    public DataBaseDto getDataBaseInfo(Connection connection) {
        DataBaseDto dataBase = new DataBaseDto();
        dataBase.setTables(getAllTablesData(connection));
        return dataBase;
    }
    
    @Override
    public List<TableDto> getAllTablesData(Connection connection) {
        List<TableDto> tables = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tablesMD = metaData.getTables(
                  null,
                  null,
                  "%",
                  new String[]{"TABLE"});
            
            while (tablesMD.next()) {
                TableDto table = new TableDto();
                table.setName(tablesMD.getString("TABLE_NAME"));
                table.setFields(getAllFields(connection, tablesMD.getString("TABLE_NAME")));
                table.setForeignKeys(getForeignKeys(connection.getMetaData(), tablesMD.getString("TABLE_NAME")));
                tables.add(table);
            }
        }
        catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return tables;
    }
    
    private List<FieldDto> getAllFields(Connection connection, String tableName) {
        List<FieldDto> fields = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName);
            
            List<String> primaryKeys = getPrimaryKeys(connection.getMetaData(), tableName);
            
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                assert primaryKeys != null;
                FieldDto field = new FieldDto(
                      rsmd.getColumnName(i),
                      PostgresTypes.valueOfLabel(rsmd.getColumnTypeName(i)),
                      primaryKeys.contains(rsmd.getColumnName(i))
                );
                fields.add(field);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return fields;
    }
    
    private List<ForeignKey> getForeignKeys(DatabaseMetaData metaData, String tableName) {
        List<ForeignKey> foreignKeys = new ArrayList<>();
        try {
            ResultSet rs = null;
            rs = metaData.getImportedKeys(null, null, tableName);
            while (rs.next()) {
                ForeignKey foreignKey = new ForeignKey();
                foreignKey.setToTable(rs.getString("PKTABLE_NAME"));
                foreignKey.setFieldsFrom(rs.getString("FKCOLUMN_NAME"));
                foreignKey.setFieldsTo(rs.getString("PKCOLUMN_NAME"));
                foreignKeys.add(foreignKey);
            }
            if (foreignKeys.isEmpty()) {
                return null;
            }
            return foreignKeys;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private List<String> getPrimaryKeys(DatabaseMetaData metaData, String tableName) {
        List<String> list = new ArrayList<>();
        try {
            ResultSet rs = null;
            rs = metaData.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                list.add(rs.getString("COLUMN_NAME"));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        
        return list;
    }
}
