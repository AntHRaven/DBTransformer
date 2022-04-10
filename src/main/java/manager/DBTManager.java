package manager;

import database.Database;
import java.sql.SQLException;

import dto.DatabaseDTO;
import transformer.DBTransformer;
import java.util.List;

public class DBTManager {
    
    public void merge(List<Database> databaseList) throws SQLException {
    
    }
    
    public void transform(Database from, Database to) throws SQLException {
        DBTransformer transformer = to.getTransformer();
        transformer.transform(from, to);
    }
    
    public void transform(DatabaseDTO from, Database to) throws SQLException {
        DBTransformer transformer = to.getTransformer();
        transformer.transform(from, to);
        
    }
    
    public DBTManager(){}
    
}
