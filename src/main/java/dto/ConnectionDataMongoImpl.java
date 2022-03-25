package dto;

import databases.dataBases.MongoDB;
import readers.MongoDBReader;
import readers.interfaces.DBReader;
import transformers.ToMongoDBTransformer;

import java.sql.Connection;

public class ConnectionDataMongoImpl implements ConnectionData<MongoDBReader, ToMongoDBTransformer>{
    
    @Override
    public Connection getConnection() {
        return null;
    }
    
    @Override
    public MongoDBReader getReader() {
        return new MongoDBReader(getConnection());
    }
    
    @Override
    public ToMongoDBTransformer getTransformer() {
        return new ToMongoDBTransformer();
    }
    
}
