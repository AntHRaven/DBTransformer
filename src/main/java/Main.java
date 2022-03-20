import java.sql.Connection;
import readers.PostgresDBReader;
import transformers.TransformerToMongoDB;

public class Main {
  public static void main(String[] args) {


    // imagine, user has postgreSQL and want MongoDB

    // connection to user's db (postgre)
    Connection c = null;

    // initialize transformator for current db (mongo)
    // call method (from relational to doc) and give it as arg -- postgre reader
    // think over what will we return to user ? new connection to mongo ?
  }
}
