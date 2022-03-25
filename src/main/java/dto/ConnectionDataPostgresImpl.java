package dto;

import databases.dataBases.PostgresSQLDB;
import readers.PostgresDBReader;
import readers.interfaces.DBReader;
import transformers.ToPostgreSQLDBTransformer;

import java.sql.Connection;

public class ConnectionDataPostgresImpl implements ConnectionData<PostgresDBReader, ToPostgreSQLDBTransformer>{
    
    
    public Connection getConnection(){
        return null;
    }
    
    @Override
    public PostgresDBReader getReader() {
        return new PostgresDBReader(getConnection());
    }
    
    @Override
    public ToPostgreSQLDBTransformer getTransformer() {
        return new ToPostgreSQLDBTransformer();
    }
}
