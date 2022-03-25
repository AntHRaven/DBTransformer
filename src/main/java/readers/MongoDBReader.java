package readers;

import java.sql.Connection;
import readers.abstractions.AbstractDocumentaryDBReader;
import readers.interfaces.DBReader;

public class MongoDBReader implements DBReader {
    public MongoDBReader(Connection connection) {
        super(connection);
    }
    
    
}
