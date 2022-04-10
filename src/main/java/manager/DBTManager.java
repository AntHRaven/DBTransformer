package manager;

import database.Database;
import java.sql.SQLException;
import transformer.DBTransformer;
import java.util.List;

public class DBTManager {
    
    public static void merge(List<Database> databaseList) throws SQLException {
        
    }
    
    public static void transform(Database from, Database to) throws SQLException {
        
        DBTransformer transformer = to.getTransformer();
        transformer.transform(from, to);
        
    }
    
    public DBTManager(){}
    
}
