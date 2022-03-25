package connection;
import readers.MongoDBReader;
import transformers.ToMongoDBTransformer;

import java.sql.Connection;

public class MongoDBConnectionData implements ConnectionData<MongoDBReader, ToMongoDBTransformer>{
    
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
