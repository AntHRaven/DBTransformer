package com.project.database;

import com.project.dto.DatabaseDTO;
import com.project.dto.FieldDTO;
import com.project.dto.ForeignKeyDTO;
import com.project.dto.TableDTO;
import com.project.transformer.DBTransformer;
import com.project.transformer.ToMongoDBTransformer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class MongoDatabase extends Database {
    
    public MongoDatabase(Connection connection) throws SQLException {
        super(connection);
        this.dbTransformer = new ToMongoDBTransformer();
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
    
    @Override
    protected ArrayList<TableDTO> getAllTables() throws SQLException {
        return null;
    }
    
    @Override
    protected ArrayList<String> getAllTablesNames() throws SQLException {
        return null;
    }
    
    @Override
    protected ArrayList<String> getTablePrimaryKeys(String tableName) throws SQLException {
        return null;
    }
    
    @Override
    protected ArrayList<FieldDTO> getAllTableFields(String tableName) throws SQLException {
        return null;
    }
    
    @Override
    protected boolean isPrimary(String columnName, String tableName) throws SQLException {
        return false;
    }
    
    @Override
    protected ForeignKeyDTO getFK(String columnName, String tableName) throws SQLException {
        return null;
    }
    
    
}
