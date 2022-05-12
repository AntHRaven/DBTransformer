import database.Database;
import database.PostgreSQL;
import manager.DBTManager;
import org.postgresql.ds.PGConnectionPoolDataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    
    public static void main(String[] args) throws SQLException, InterruptedException {
        
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
        
        Database postgre1 = new PostgreSQL("firstDataBase", postgresqlConnectionFirst, new ArrayList<>(List.of("usr, message")));
        Database postgre2 = new PostgreSQL("secondDataBase", postgresqlConnectionSecond, new ArrayList<>(List.of("usr, message")));
        
        DBTManager dbtManager = new DBTManager();
        dbtManager.transform(postgre1, postgre2);
        
        List<String> list = new ArrayList<>();
        list.add("usr");
        list.add("message");
        
        System.out.println(list.contains("usr"));
    }
}

