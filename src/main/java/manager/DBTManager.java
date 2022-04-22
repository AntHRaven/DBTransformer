package manager;

import database.Database;
import java.sql.SQLException;

import dto.DatabaseDTO;
import merger.DBMerger;
import transformer.DBTransformer;

import java.util.ArrayList;
import java.util.List;

public class DBTManager {
    
    public void merge(List<DatabaseDTO> databaseList, Database to) throws SQLException {
        DBMerger merger = to.getMerger();
        merger.merge(merger.getMergedDto(databaseList, to), to);
    }
    
    public void transform(Database from, Database to) throws SQLException {
        DBTransformer transformer = to.getTransformer();
        transformer.transform(from, to);
    }
    
    public void transform(DatabaseDTO from, Database to) throws SQLException {
        DBTransformer transformer = to.getTransformer();
        transformer.transform(from, to);
        
    }
    
    
}
