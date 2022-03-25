import databases.dataBases.PostgresSQLDB;
import dto.ConnectionData;
import readers.PostgresDBReader;
import transformers.ToPostgreSQLDBTransformer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DBManagerPostgresImpl
      extends DbManager{
    
    
    @Override
    Connection merge(List<? extends ConnectionData> connectionDataList) throws SQLException {
        return null;
    }
    
    @Override
    Connection transform(ConnectionData connectionData) throws SQLException {
    
        ToPostgreSQLDBTransformer transformer = new ToPostgreSQLDBTransformer();
        PostgresDBReader reader = DBReaderManager.getReader(connectionData);
        
        return null;
    }
}
