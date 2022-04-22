package merger;
import database.Database;
import dto.DatabaseDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DBMerger {
    
    void  merge(DatabaseDTO from, Database to) throws SQLException;
    
    DatabaseDTO getMergedDto(List<DatabaseDTO> databaseList, Database to) throws SQLException;
}
