package com.project.database;

import com.project.dto.DatabaseDTO;
import com.project.dto.FieldDTO;
import com.project.dto.ForeignKeyDTO;
import com.project.dto.TableDTO;
import com.project.transformer.DBTransformer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Database {
   
    protected DBTransformer dbTransformer;
    protected Connection connection;
    protected DatabaseMetaData metaData;
    protected DatabaseDTO databaseDTO;
    
    public Database(Connection connection) throws SQLException {
        this.connection = connection;
        metaData = connection.getMetaData();
    };
    
    public Connection getConnection(){return this.connection;}
    public DatabaseDTO getDatabaseDTO(){return this.databaseDTO;}
    
    abstract public <T extends DBTransformer> T getTransformer();
    
    abstract protected ArrayList<TableDTO> getAllTables() throws SQLException;
    
    abstract protected ArrayList<String> getAllTablesNames() throws SQLException;
    
    abstract protected ArrayList<FieldDTO> getAllTableFields(String tableName) throws SQLException;
    
    abstract protected ArrayList<String> getTablePrimaryKeys(String tableName) throws SQLException;
    
    abstract protected boolean isPrimary(String columnName, String tableName) throws SQLException;
    
    abstract protected ForeignKeyDTO getFK(String columnName, String tableName) throws SQLException;
    
    abstract protected DatabaseDTO makeDTO() throws SQLException;
}
