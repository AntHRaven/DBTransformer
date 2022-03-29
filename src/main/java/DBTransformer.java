import connection.DataBase;
import connection.MongoDB;
import connection.PostgresDB;
import managers.DBManagerImpl;
import readers.impl.MongoDBReader;
import readers.impl.PostgresDBReader;
import transformers.impl.ToMongoDBTransformer;
import transformers.impl.ToPostgresDBTransformer;

import java.sql.SQLException;
import utils.fieldTypes.PostgresTypes;
import utils.typesConverter.TypesConverter;
import utils.typesConverter.impl.PostgresConverter;

public class DBTransformer {

  public static void main(String[] args) throws SQLException {
    test();

  }

  public static void test() throws SQLException {
    DataBase<PostgresDBReader, ToPostgresDBTransformer> from = new PostgresDB(
          "jdbc:postgresql://ec2-52-209-185-5.eu-west-1.compute.amazonaws.com:5432/d8lbn6g1mlieem",
          "mjjqqxjrytjlac",
          "66ea5529ab3eae3617f373c5d65633f6479d378f6c5a9c6451bfefeee28287ed"
    );
    DataBase<PostgresDBReader, ToPostgresDBTransformer> to = new PostgresDB(
        "jdbc:postgresql://localhost:5432/db_converter_postgres",
        "postgres",
        "12345"
    );

    DBManagerImpl dbManager = new DBManagerImpl();
    dbManager.transform(from, to);

    TypesConverter<PostgresTypes> converter = new PostgresConverter();
  }
}
