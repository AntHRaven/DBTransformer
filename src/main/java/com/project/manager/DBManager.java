package com.project.manager;

import com.project.database.Database;
import java.sql.SQLException;
import com.project.transformer.DBTransformer;
import java.util.List;

public class DBManager{
    
    public void merge(List<Database> databaseList) throws SQLException {
        
    }
    
    public void transform(Database from, Database to) throws SQLException {
        
        DBTransformer transformer = to.getTransformer();
        transformer.transform(from);
        
    }
    
}
