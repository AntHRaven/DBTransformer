package manager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import database.Database;
import database.MongoDB;
import database.PostgreSQL;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBTManagerTest {

  DBTManager dbtManager = new DBTManager();

  @Test
  void transformToPostgreSQLTest() throws SQLException, InterruptedException {
    List<String> list = new ArrayList<>();
    list.add("test");

    String client_url = "mongodb://root:rootpassword@localhost:27017";
    MongoClientURI uri = new MongoClientURI(client_url);
    MongoClient mongoClient = new MongoClient(uri);

    PGConnectionPoolDataSource postgresqlConnectionSecond = new PGConnectionPoolDataSource();
    postgresqlConnectionSecond.setURL("jdbc:postgresql://localhost:5432/secondDataBase");
    postgresqlConnectionSecond.setUser("postgres");
    postgresqlConnectionSecond.setPassword("12345");

    Database postgre = new PostgreSQL("secondDataBase", postgresqlConnectionSecond, list);
    Database mongo = new MongoDB("admin", mongoClient, list);
    dbtManager.transform(mongo, postgre);

  }

  @Test
  void transformToMongoDBTest() throws SQLException, InterruptedException {
    List<String> list = new ArrayList<>();
    list.add("message");
    list.add("usr");

    String client_url = "mongodb://root:rootpassword@localhost:27017";
    MongoClientURI uri = new MongoClientURI(client_url);
    MongoClient mongoClient = new MongoClient(uri);

    PGConnectionPoolDataSource postgresqlConnectionFirst = new PGConnectionPoolDataSource();
    postgresqlConnectionFirst.setURL("jdbc:postgresql://localhost:5432/firstDataBase");
    postgresqlConnectionFirst.setUser("postgres");
    postgresqlConnectionFirst.setPassword("12345");

    Database postgre = new PostgreSQL("firstDataBase", postgresqlConnectionFirst, list);
    Database mongo = new MongoDB("admin", mongoClient, list);
    dbtManager.transform(postgre, mongo);
  }
}
