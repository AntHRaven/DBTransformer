import database.Database;
import database.PostgreSQL;
import manager.DBTManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    
    public static void main(String[] args) throws SQLException {
        
        Connection postgresqlConnectionFrom = DriverManager.getConnection(
              "jdbc:postgresql://ec2-52-209-185-5.eu-west-1.compute.amazonaws.com:5432/d8lbn6g1mlieem",
              "mjjqqxjrytjlac",
              "66ea5529ab3eae3617f373c5d65633f6479d378f6c5a9c6451bfefeee28287ed");
        Connection postgresqlConnectionTo = DriverManager.getConnection(
              "jdbc:postgresql://localhost:5432/db_converter_postgres",
              "postgres",
              "12345");
        Database from = new PostgreSQL(postgresqlConnectionFrom);
        Database to = new PostgreSQL(postgresqlConnectionTo);
        
        DBTManager DBTManager = new DBTManager();
        DBTManager.transform(from, to);
    }
}
