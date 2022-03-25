import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import readers.PostgresDBReader;
import transformers.TransformerToMongoDB;
import transformers.interfaces.TransformerToDocumentaryDB;

public class DBTransformer {
  public static void main(String[] args) {

    Connection connection = null;
    try {
      connection = DriverManager.getConnection(
          "jdbc:postgresql://localhost:5432/db_converter_postgres",
          "postgres",
          "12345"
      );
    } catch (SQLException e) {
      System.out.println("ОШИБКА ПОДКЛЮЧЕНИЯ К БД:\n" + e.getMessage());
    }
    TransformerToDocumentaryDB transformer = new TransformerToMongoDB();
    transformer.fromRelationalToDocumentary(new PostgresDBReader(connection));
  }
}
