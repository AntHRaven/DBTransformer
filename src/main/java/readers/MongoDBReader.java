package readers;

import java.sql.Connection;
import readers.abstractions.AbstractDocumentaryDBReader;

public class MongoDBReader extends AbstractDocumentaryDBReader {
    public MongoDBReader(Connection connection) {
        super(connection);
    }
}
