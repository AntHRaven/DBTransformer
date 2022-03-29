package readers.impl;

import dto.TableDto;
import java.sql.Connection;
import java.util.List;

import dto.FieldDto;
import readers.DBReader;

public class MongoDBReader implements DBReader {

    public MongoDBReader(Connection connection) {
    }

    @Override
    public List<TableDto> getAllTablesNames(Connection connection) {
        return null;
    }

    @Override
    public List<FieldDto> getAllFields(Connection connection, String tableName) {
        return null;
    }
}
