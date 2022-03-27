import connection.ConnectionData;
import connection.MongoDBConnectionData;
import connection.PostgresSQLDBConnectionData;

import managers.DBManagerImpl;

import readers.impl.MongoDBReader;
import readers.impl.PostgresSQLDBReader;
import transformers.impl.ToMongoDBTransformer;
import transformers.impl.ToPostgresSQLDBTransformer;

import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException {
    ConnectionData<MongoDBReader, ToMongoDBTransformer> from = new MongoDBConnectionData();
    ConnectionData<PostgresSQLDBReader, ToPostgresSQLDBTransformer> to = new PostgresSQLDBConnectionData();
    DBManagerImpl dbManager = new DBManagerImpl();
    dbManager.transform(from, to);
  }
}
