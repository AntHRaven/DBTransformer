package readers.abstractions;

import java.sql.Connection;
import readers.interfaces.KeyValueDBReader;

public abstract class AbstractKeyValueDBReader implements KeyValueDBReader {
    protected Connection connection;

    protected AbstractKeyValueDBReader(Connection connection){

    }
}
