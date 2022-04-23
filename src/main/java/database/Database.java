package database;

import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import merger.DBMerger;
import transformer.DBTransformer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Database {
    
    protected List<String> names;
    protected DBTransformer dbTransformer;
    protected DBMerger dbMerger;
    
    abstract public <T extends DBTransformer> T getTransformer();
    
    abstract public <T extends DBMerger> T getMerger();
    
    abstract public DatabaseDTO makeDTO() throws SQLException;
    
    Database(List<String> names){
        this.names = names;
    }
}
