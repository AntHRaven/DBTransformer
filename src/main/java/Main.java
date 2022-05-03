import database.Database;
import database.PostgreSQL;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import manager.DBTManager;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    
    public static void main(String[] args) throws SQLException {
        
        PGConnectionPoolDataSource postgresqlConnectionFrom = new PGConnectionPoolDataSource();
        postgresqlConnectionFrom.setURL("jdbc:postgresql://ec2-52-209-185-5.eu-west-1.compute.amazonaws.com:5432/d8lbn6g1mlieem");
        postgresqlConnectionFrom.setUser("mjjqqxjrytjlac");
        postgresqlConnectionFrom.setPassword("66ea5529ab3eae3617f373c5d65633f6479d378f6c5a9c6451bfefeee28287ed");
//
        PGConnectionPoolDataSource postgresqlConnectionTo = new PGConnectionPoolDataSource();
        postgresqlConnectionTo.setURL("jdbc:postgresql://localhost:5432/db_converter_postgres");
        postgresqlConnectionTo.setUser("postgres");
        postgresqlConnectionTo.setPassword("12345");
        
        PGConnectionPoolDataSource postgresqlConnectionMerged = new PGConnectionPoolDataSource();
        postgresqlConnectionMerged.setURL("jdbc:postgresql://localhost:5432/testMergedDB");
        postgresqlConnectionMerged.setUser("postgres");
        postgresqlConnectionMerged.setPassword("12345");
        
        Database postgre1 = new PostgreSQL(postgresqlConnectionFrom);
        Database postgre2 = new PostgreSQL(postgresqlConnectionTo);
        Database merged = new PostgreSQL(postgresqlConnectionMerged);
        List<DatabaseDTO> list = new ArrayList<>();
        list.add(postgre1.makeDTO());
        list.add(postgre2.makeDTO());
        DBTManager DBTManager = new DBTManager();
        for (TableDTO tableDTO : DBTManager.getMergedDto(list).getTables()) {
            System.out.println(tableDTO.getName());
            for (FieldDTO fieldDTO : tableDTO.getFields()) {
                System.out.println(fieldDTO);
            }
        }
//        DBTManager.merge(list, merged);
    
    }
}
