package readers;

import java.sql.Connection;
import java.util.List;
import models.FieldModel;

public interface DBReader {

  List<String> getAllTablesNames(Connection connection);

  List<FieldModel> getAllFields(Connection connection, String tableName);
  // common behavior for all readers
}
