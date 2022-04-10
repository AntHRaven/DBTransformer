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
    
    abstract public <T extends DBTransformer> T getTransformer();
    
    abstract public DatabaseDTO makeDTO() throws SQLException;
}
