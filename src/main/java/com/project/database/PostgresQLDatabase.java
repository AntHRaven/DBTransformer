package com.project.database;

import com.project.dto.DatabaseDTO;
import com.project.dto.FieldDTO;
import com.project.dto.ForeignKeyDTO;
import com.project.dto.TableDTO;
import com.project.transformer.DBTransformer;
import com.project.transformer.ToPostgresDBTransformer;

import java.sql.*;
import java.util.ArrayList;

public class PostgresQLDatabase extends Database{
    
    public PostgresQLDatabase(Connection connection) throws SQLException {
        super(connection);
        this.dbTransformer = new ToPostgresDBTransformer();
        makeDTO();
    }
    
    @Override
    public DBTransformer getTransformer() {
        return this.dbTransformer;
    }
    
    @Override
    protected DatabaseDTO makeDTO() throws SQLException {
        return new DatabaseDTO(getAllTables());
    }
    
    protected ArrayList<TableDTO> getAllTables() throws SQLException {
        ArrayList<TableDTO> tables = new ArrayList<>();
        for (String tableName : getAllTablesNames()) {
            TableDTO tableDTO = new TableDTO(tableName, getAllTableFields(tableName));
            tables.add(tableDTO);
        }
        return tables;
    }
    
    @Override
    protected ArrayList<String> getAllTablesNames() throws SQLException {
        ArrayList<String> tablesNames = new ArrayList<>();
        ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
        while(rs.next()) {
            tablesNames.add(rs.getString(1));
        }
        return tablesNames;
    }
    
    @Override
    protected ArrayList<FieldDTO> getAllTableFields(String tableName) throws SQLException {
        ArrayList<FieldDTO> fields = new ArrayList<>();
        ResultSet rs = metaData.getColumns(null, null, tableName, "%");
        
        while(rs.next()) {
            String columnName = rs.getString(4);
            int columnType = rs.getInt(5);
            FieldDTO fieldDTO = new FieldDTO(columnName, columnType, isPrimary(columnName, tableName), getFK(columnName, tableName));
            fields.add(fieldDTO);
        }
        return fields;
    }
    
    @Override
    protected ArrayList<String> getTablePrimaryKeys(String tableName) throws SQLException {
        ArrayList<String> primaryKeys = new ArrayList<>();
        ResultSet rs = metaData.getPrimaryKeys(null, null, tableName);
        while(rs.next()){
            primaryKeys.add(rs.getString(4));
        }
        return primaryKeys;
    }
    
    @Override
    protected boolean isPrimary(String columnName, String tableName) throws SQLException {
        for (String key : getTablePrimaryKeys(tableName)) {
            if (columnName.equals(key)) return true;
        }
        return false;
    }
    
    @Override
    protected ForeignKeyDTO getFK(String columnName, String tableName) throws SQLException {
        ResultSet rs = metaData.getImportedKeys(null, null, tableName);
        while (rs.next()) {
            if (columnName.equals(rs.getString("FKCOLUMN_NAME"))){
                return new ForeignKeyDTO(rs.getString("PKTABLE_NAME"), rs.getString("PKCOLUMN_NAME"));
            }
        }
        return null;
    }

    
}
