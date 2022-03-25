package managers;

import connection.ConnectionData;
import readers.interfaces.DBReader;
import transformers.DBTransformer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IDBManager {
    void merge(List<ConnectionData<? extends DBReader, ? extends DBTransformer>> connectionDataList)
            throws SQLException;
    void transform(ConnectionData<? extends DBReader, ? extends DBTransformer> from,
                   ConnectionData<? extends DBReader, ? extends DBTransformer> to)
            throws SQLException;

}