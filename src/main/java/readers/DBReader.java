package readers;

import dto.DataBaseDto;
import dto.TableDto;

import java.sql.Connection;
import java.util.List;

import dto.FieldDto;

public interface DBReader {
    
    DataBaseDto getDataBaseInfo(Connection connection);
    
    List<TableDto> getAllTablesData(Connection connection);
    
//    List<FieldDto> getAllFields(Connection connection, String tableName);
    // common behavior for all readers
}
