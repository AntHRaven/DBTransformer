package managers;

import connection.ConnectionData;
import readers.interfaces.DBReader;
import transformers.DBTransformer;

import java.sql.SQLException;
import java.util.List;

public class DBManagerImpl implements DBManager {

    @Override
    public void merge(List<ConnectionData<? extends DBReader, ? extends DBTransformer>> connectionDataList)
            throws SQLException {

    }
    
    @Override
    public void transform(ConnectionData<? extends DBReader, ? extends DBTransformer> from,
                          ConnectionData<? extends DBReader, ? extends DBTransformer> to)
            throws SQLException {
    
        DBReader reader = from.getReader();
        DBTransformer transformer = to.getTransformer();

        transformer.transform(reader);
    }
}
