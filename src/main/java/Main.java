import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import database.Database;
import database.MongoDB;
import database.PostgreSQL;
import dto.DatabaseDTO;
import dto.TableDTO;
import manager.DBTManager;
import org.postgresql.ds.PGConnectionPoolDataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    
    public static void main(String[] args) throws SQLException, InterruptedException, ClassNotFoundException {
        
        PGConnectionPoolDataSource postgresqlConnectionFirst = new PGConnectionPoolDataSource();
        postgresqlConnectionFirst.setURL("jdbc:postgresql://localhost:5432/firstDataBase");
        postgresqlConnectionFirst.setUser("postgres");
        postgresqlConnectionFirst.setPassword("12345");
        
        PGConnectionPoolDataSource postgresqlConnectionSecond = new PGConnectionPoolDataSource();
        postgresqlConnectionSecond.setURL("jdbc:postgresql://localhost:5432/secondDataBase");
        postgresqlConnectionSecond.setUser("postgres");
        postgresqlConnectionSecond.setPassword("12345");
        
        PGConnectionPoolDataSource postgresqlConnectionMerged = new PGConnectionPoolDataSource();
        postgresqlConnectionMerged.setURL("jdbc:postgresql://localhost:5432/testMergedDB");
        postgresqlConnectionMerged.setUser("postgres");
        postgresqlConnectionMerged.setPassword("12345");
        
        String client_url = "mongodb://root:rootpassword@localhost:27017";
        MongoClientURI uri = new MongoClientURI(client_url);
        
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("DBName");
        
        List<String> list = new ArrayList<>();
        list.add("usr");
        list.add("message");
        list.add("test");
        
        List<String> mongoList = new ArrayList<>();
        mongoList.add("admin");
        
        Database postgre1 = new PostgreSQL("firstDataBase", postgresqlConnectionFirst, list);
        Database postgre2 = new PostgreSQL("secondDataBase", postgresqlConnectionSecond, list);
        Database mongo = new MongoDB("admin", mongoClient, list);
        DBTManager dbtManager = new DBTManager();
        DatabaseDTO databaseDTO = postgre1.makeDTO();
        mongo.makeDTO().getTables().forEach((item) -> {
            System.out.println(item.getName());
        });
        System.out.println(postgre1.makeDTO());
//        dbtManager.transform(mongo, postgre2);
        dbtManager.transform(postgre1, mongo);
        System.out.println("DONE");
    }
}

