package readers.impl;

import dto.DataBaseDto;
import dto.TableDto;

import java.sql.Connection;
import java.util.List;

import dto.FieldDto;
import readers.DBReader;

public class MongoDBReader
      implements DBReader {
    
    public MongoDBReader(Connection connection) {
    }
    
    @Override
    public DataBaseDto getDataBaseInfo(Connection connection) {
        return null;
    }
    
    @Override
    public List<TableDto> getAllTablesData(Connection connection) {
        return null;
    }
    
}
