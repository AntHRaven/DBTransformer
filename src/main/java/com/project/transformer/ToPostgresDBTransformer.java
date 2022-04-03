package com.project.transformer;

import java.sql.SQLException;

import com.project.database.Database;
import com.project.dto.DatabaseDTO;

public class ToPostgresDBTransformer implements DBTransformer {
    @Override
    public void transform(Database from) throws SQLException {
        DatabaseDTO databaseDTO = from.getDatabaseDTO();
    }
    
}
