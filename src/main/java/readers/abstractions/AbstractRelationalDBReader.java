package readers.abstractions;

import java.sql.Connection;
import readers.interfaces.RelationalDBReader;


public abstract class AbstractRelationalDBReader implements RelationalDBReader {
    protected Connection connection;

    protected AbstractRelationalDBReader(Connection connection){

    }
}
