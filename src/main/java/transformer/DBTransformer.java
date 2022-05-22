package transformer;

import dto.DatabaseDTO;
import java.sql.SQLException;
import database.Database;

public interface DBTransformer {

  void transform(Database from, Database to) throws SQLException, InterruptedException;
  void transform(DatabaseDTO from, Database to) throws SQLException, InterruptedException;
  //specific for merge
}

