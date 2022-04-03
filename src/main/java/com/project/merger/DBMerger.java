package com.project.merger;
import com.project.database.Database;

import java.sql.Connection;
import java.util.List;

public interface DBMerger {
    
    Connection merge(List<Database> databaseList);
    
}
