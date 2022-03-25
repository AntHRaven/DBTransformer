import connection.ConnectionData;
import connection.MongoDBConnectionData;
import connection.PostgreSQLDBConnectionData;
import managers.DBManager;
import readers.MongoDBReader;
import readers.PostgreSQLDBReader;
import transformers.ToMongoDBTransformer;
import transformers.ToPostgreSQLDBTransformer;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        ConnectionData<MongoDBReader, ToMongoDBTransformer> from = new MongoDBConnectionData();
        ConnectionData<PostgreSQLDBReader, ToPostgreSQLDBTransformer> to = new PostgreSQLDBConnectionData();
        DBManager dbManager = new DBManager();
        dbManager.transform(from, to);
    }
}
