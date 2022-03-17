package readers;

import java.sql.Connection;
import java.util.ArrayList;
import readers.abstractions.AbstractRelationalDBReader;

public class PostgresDBReader extends AbstractRelationalDBReader {

    // constructor from super class !
    public PostgresDBReader(Connection connection) {
        super(connection);
    }

    // realisation of methods, which weren't realised in super class

    // these methods allow us to read user's db
    // and get all what we need to transform db to another one

    // these methods we will use in transformer (from postgre to another db)

    @Override
    public ArrayList<String> getAllTablesNames() {
        return null;
    }

    @Override
    public ArrayList<String> getAllFieldsNames(String tableName) {
        return null;
    }

    @Override
    public ArrayList<String> getPrimaryKeyColumnsNames(String tableName) {
        return null;
    }

    @Override
    public ArrayList<String> getForeignKeyColumnsNames(String tableName) {
        return null;
    }
}
