package managers;

import connection.ConnectionData;
import readers.DBReader;
import transformers.DBTransformer;

import java.sql.SQLException;
import java.util.List;

public class DBManagerImpl
      implements DBManager {
    
    @Override
    public void merge(
          List<ConnectionData<? extends DBReader, ? extends DBTransformer>> connectionDataList) {
        
    }
    
    @Override
    public void transform(ConnectionData<? extends DBReader, ? extends DBTransformer> from,
                          ConnectionData<? extends DBReader, ? extends DBTransformer> to) {
        
        DBReader reader = from.getReader();
        DBTransformer transformer = to.getTransformer();
        List<String> tables = reader.getAllTablesNames(from.getConnection());
        for (String table : tables) {
            System.out.println("FIELDS: " + from.getReader().getAllFields(from.getConnection(), table));
        }
        transformer.transform(reader);
    }
}
