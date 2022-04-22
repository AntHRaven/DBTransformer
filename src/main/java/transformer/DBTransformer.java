package transformer;

import java.sql.SQLException;
import java.util.List;

import database.Database;
import dto.DatabaseDTO;

public interface DBTransformer {

    void transform(Database from, Database to) throws SQLException;
    
    void transform(DatabaseDTO from, Database to) throws SQLException;
    
}
