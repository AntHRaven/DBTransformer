package connection;

import readers.impl.PostgresSQLDBReader;
import transformers.impl.ToPostgresSQLDBTransformer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresSQLDBConnectionData
      extends ConnectionData<PostgresSQLDBReader, ToPostgresSQLDBTransformer> {
    
    
    public PostgresSQLDBConnectionData(String url) {
        super(url);
    }
    
    public PostgresSQLDBConnectionData(String url, String user, String password) {
        super(url, user, password);
    }
    
    @Override
    public Connection getConnection() {
        System.out.println("URL:" + url);
        System.out.println("USER:" + user);
        System.out.println("PASSWORD:" + password);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        System.out.println("RETURN: " + connection);
        return connection;
    }
    
    @Override
    public PostgresSQLDBReader getReader() {
        return new PostgresSQLDBReader();
    }
    
    @Override
    public ToPostgresSQLDBTransformer getTransformer() {
        return new ToPostgresSQLDBTransformer();
    }
}
