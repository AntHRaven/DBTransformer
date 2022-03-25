package connection;
import readers.PostgreSQLDBReader;
import transformers.ToPostgreSQLDBTransformer;

import java.sql.Connection;

public class PostgreSQLDBConnectionData implements ConnectionData<PostgreSQLDBReader, ToPostgreSQLDBTransformer>{
    
    
    public Connection getConnection(){
        return null;
    }
    
    @Override
    public PostgreSQLDBReader getReader() {
        return new PostgreSQLDBReader(getConnection());
    }
    
    @Override
    public ToPostgreSQLDBTransformer getTransformer() {
        return new ToPostgreSQLDBTransformer();
    }
}
