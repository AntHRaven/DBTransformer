package utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface FileUtils {
  void clearFile(File file) throws IOException;
  void fillSqlFile(String query, String filename) throws IOException;
  void fillSqlFile(List<String> queues, String filename);
  void executeSqlFile(String fileName, Connection connectionTo) throws IOException, SQLException;
}
