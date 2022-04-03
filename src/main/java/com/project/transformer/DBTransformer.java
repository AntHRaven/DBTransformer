package com.project.transformer;

import java.sql.SQLException;

import com.project.database.Database;

public interface DBTransformer {

    void transform(Database from) throws SQLException;
    
}
