package readers.impl;

import java.sql.Connection;
import java.util.List;

import models.FieldModel;
import readers.DBReader;

public class MongoDBReader implements DBReader {

    public MongoDBReader(Connection connection) {
    }

    @Override
    public List<String> getAllTablesNames(Connection connection) {
        return null;
    }
    
    @Override
    public List<FieldModel> getAllFields(Connection connection, String tableName) {
        return null;
    }
}
