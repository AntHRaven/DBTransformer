import databases.dataBases.PostgresSQLDB;
import dto.ConnectionData;
import dto.ConnectionDataMongoImpl;
import dto.ConnectionDataPostgresImpl;
import readers.PostgresDBReader;

import java.sql.SQLException;

public class Main {
    public static void main(ConnectionData<PostgresSQLDB> connection) throws SQLException {
        //DbManager dbManager = new DBManagerPostgresImpl();
        //dbManager.transform(connection);
    
        PostgresDBReader reader = DBReaderManager.getReader(new ConnectionDataPostgresImpl());
    
        ConnectionDataMongoImpl connectionDataMongo = new ConnectionDataMongoImpl();
    }
}
