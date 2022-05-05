package database;

import dto.DatabaseDTO;
import transformer.DBTransformer;
import java.sql.SQLException;
import java.util.List;

public abstract class Database {
    
    protected List<String> names;
    protected DBTransformer dbTransformer;
    
    abstract public <T extends DBTransformer> T getTransformer();
    
    abstract public DatabaseDTO makeDTO() throws SQLException;
    
    Database(List<String> names){
        this.names = names;
    }
    
    public List<String> getNames() {
        return names;
    }
}
