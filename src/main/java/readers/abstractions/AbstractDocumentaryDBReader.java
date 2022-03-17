package readers.abstractions;

import java.sql.Connection;
import readers.interfaces.DocumentaryDBReader;

public abstract class AbstractDocumentaryDBReader implements DocumentaryDBReader {
    protected Connection connection;

    protected AbstractDocumentaryDBReader(Connection connection){

    }
}
