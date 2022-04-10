import com.mongodb.MongoClient;
import database.Database;
import database.MongoDB;
import database.PostgreSQL;
import manager.DBTManager;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    
    public static void main(String[] args) throws SQLException, UnknownHostException {
        MongoClient mongoClient = new MongoClient("");
        Connection postgresqlConnection = DriverManager.getConnection("", "", "");
        
        Database from = new MongoDB(mongoClient);
        Database to = new PostgreSQL(postgresqlConnection);
        
        DBTManager DBTManager = new DBTManager();
        DBTManager.transform(from, to);
    }
}
