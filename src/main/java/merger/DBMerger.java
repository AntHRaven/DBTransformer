package merger;

import database.Database;
import dto.DatabaseDTO;
import java.sql.SQLException;
import java.util.List;

public interface DBMerger {

  void merge(Database from, Database to) throws SQLException, InterruptedException;

  DatabaseDTO getMergedDto(List<Database> databaseList, Database to) throws SQLException;
}
