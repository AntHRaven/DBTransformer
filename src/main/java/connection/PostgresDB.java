package connection;

import readers.impl.PostgresDBReader;
import transformers.impl.ToPostgresDBTransformer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDB
    extends DataBase<PostgresDBReader, ToPostgresDBTransformer> {


  public PostgresDB(String url) {
    super(url);
  }

  public PostgresDB(String url, String user, String password) {
    super(url, user, password);
  }

  @Override
  public Connection getConnection() {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(url, user, password);
      System.out.println("Connected to the PostgreSQL server successfully.");
    } catch (SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
    }
    return connection;
  }

  @Override
  public PostgresDBReader getReader() {
    return new PostgresDBReader();
  }

  @Override
  public ToPostgresDBTransformer getTransformer() {
    return new ToPostgresDBTransformer();
  }
}
