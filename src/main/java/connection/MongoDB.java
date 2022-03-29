package connection;

import readers.impl.MongoDBReader;
import transformers.impl.ToMongoDBTransformer;

import java.sql.Connection;

public class MongoDB
      extends DataBase<MongoDBReader, ToMongoDBTransformer> {
    
    public MongoDB(String url) {
        super(url);
    }
    
    public MongoDB(String url, String user, String password) {
        super(url, user, password);
    }
    
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
