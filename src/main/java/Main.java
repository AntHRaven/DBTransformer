import com.project.database.Database;
import com.project.database.MongoDatabase;
import com.project.database.PostgresQLDatabase;
import com.project.manager.DBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    
    public static void main(String[] args) throws SQLException {
        Connection mongoConnection = DriverManager.getConnection("", "", "");
        Connection postgresqlConnection = DriverManager.getConnection("", "", "");
        
        Database from = new MongoDatabase(mongoConnection);
        Database to = new PostgresQLDatabase(postgresqlConnection);
        
        DBManager dbManager = new DBManager();
        dbManager.transform(from, to);
        
        mongoConnection.close();
        postgresqlConnection.close();
    }
}
