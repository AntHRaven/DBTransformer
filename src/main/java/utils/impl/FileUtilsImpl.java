package utils.impl;

import com.ibatis.common.jdbc.ScriptRunner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import utils.FileUtils;

public class FileUtilsImpl implements FileUtils {

  @Override
  public void clearFile(File file) throws IOException {
    PrintWriter writer = new PrintWriter(file);
    writer.print("");
    writer.close();
  }

  @Override
  public void fillSqlFile(String query, String filename) throws IOException {
    File file = new File(filename);
    if (!file.exists()) {
      if (!file.createNewFile()) {
        throw new FileSystemException("File not created");
      };
    }
    Files.write(Paths.get(filename), query.getBytes(), StandardOpenOption.APPEND);
  }

  @Override
  public void fillSqlFile(List<String> queues, String filename) {
    queues.forEach((query) -> {
      try {
        fillSqlFile(query, filename);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public void executeSqlFile(String fileName, Connection connectionTo) throws IOException, SQLException {
    Reader reader = new BufferedReader(new FileReader(fileName));
    ScriptRunner sr = new ScriptRunner(connectionTo, false, true);
    sr.runScript(reader);
    File file = new File(fileName);
    if(!file.delete()) {
      throw new FileSystemException("File not deleted");
    }
  }

}
