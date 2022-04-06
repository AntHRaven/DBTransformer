package transformer;

import java.sql.SQLException;

import database.Database;

public interface DBTransformer {

    void transform(Database from, Database to) throws SQLException;
    
}
