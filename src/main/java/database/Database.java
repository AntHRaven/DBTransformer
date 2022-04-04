package database;

import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import transformer.DBTransformer;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Database {
   
    protected DBTransformer dbTransformer;
    protected DatabaseDTO databaseDTO;
    
    abstract public <T extends DBTransformer> T getTransformer();
    
    abstract protected ArrayList<TableDTO> getAllTables() throws SQLException;
    
    abstract protected ArrayList<FieldDTO> getAllTableFields(String tableName) throws SQLException;
    
    abstract protected ForeignKeyDTO getFK(String columnName, String tableName) throws SQLException;
    
    abstract public DatabaseDTO makeDTO() throws SQLException;
}
