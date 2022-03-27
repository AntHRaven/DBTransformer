import connection.ConnectionData;
import connection.MongoDBConnectionData;
import connection.PostgresDBConnectionData;
import managers.DBManagerImpl;
import readers.impl.MongoDBReader;
import readers.impl.PostgresDBReader;
import transformers.impl.ToMongoDBTransformer;
import transformers.impl.ToPostgresDBTransformer;

import java.sql.SQLException;
import utils.fieldTypes.MongoDBType;
import utils.fieldTypes.PostgresTypes;
import utils.typesConverter.TypesConverter;
import utils.typesConverter.impl.PostgresConverter;

public class DBTransformer {

  public static void main(String[] args) throws SQLException {
    test();

  }

  public static void test() {
    ConnectionData<PostgresDBReader, ToPostgresDBTransformer> from = new PostgresDBConnectionData(
          "jdbc:postgresql://ec2-52-209-185-5.eu-west-1.compute.amazonaws.com:5432/d8lbn6g1mlieem",
          "mjjqqxjrytjlac",
          "66ea5529ab3eae3617f373c5d65633f6479d378f6c5a9c6451bfefeee28287ed"
    );
    ConnectionData<MongoDBReader, ToMongoDBTransformer> to = new MongoDBConnectionData("");

    DBManagerImpl dbManager = new DBManagerImpl();
    dbManager.transform(from, to);

    TypesConverter<PostgresTypes> converter = new PostgresConverter();
  }
}
