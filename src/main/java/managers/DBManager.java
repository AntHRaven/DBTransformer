package managers;

import connection.ConnectionData;
import readers.DBReader;
import transformers.DBTransformer;

import java.sql.SQLException;
import java.util.List;

public interface DBManager {
    
    void merge(List<ConnectionData<? extends DBReader, ? extends DBTransformer>> connectionDataList)
          throws SQLException;
    
    void transform(ConnectionData<? extends DBReader, ? extends DBTransformer> from,
                   ConnectionData<? extends DBReader, ? extends DBTransformer> to)
          throws SQLException;
    
}
