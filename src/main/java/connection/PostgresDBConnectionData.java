package connection;

import readers.impl.PostgresDBReader;
import transformers.impl.ToPostgresDBTransformer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDBConnectionData
    extends ConnectionData<PostgresDBReader, ToPostgresDBTransformer> {


  public PostgresDBConnectionData(String url) {
    super(url);
  }

  public PostgresDBConnectionData(String url, String user, String password) {
    super(url, user, password);
  }

  @Override
  public Connection getConnection() {
    System.out.println("URL:" + url);
    System.out.println("USER:" + user);
    System.out.println("PASSWORD:" + password);
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(url, user, password);
      System.out.println("Connected to the PostgreSQL server successfully.");
    } catch (SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
    }
    System.out.println("RETURN: " + connection);
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
