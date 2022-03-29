package readers;

import dto.TableDto;
import java.sql.Connection;
import java.util.List;
import dto.FieldDto;

public interface DBReader {

  List<TableDto> getAllTablesNames(Connection connection);

  List<FieldDto> getAllFields(Connection connection, String tableName);
  // common behavior for all readers
}
