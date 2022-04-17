package data.provider;

import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostgreSQLDataProvider extends Provider {
    
    private final Connection connection;
    
    public PostgreSQLDataProvider(Connection connection, DatabaseDTO databaseDTO){
        super(databaseDTO);
        this.connection = connection;
    }
    
    @Override
    public ArrayList<ArrayList<Map<FieldDTO, Object>>> getTableRows(TableDTO table) throws SQLException {
        ArrayList<ArrayList<Map<FieldDTO, Object>>> tableRows = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet rows = stmt.executeQuery("select count(*) from " + table.getName());
        rows.next();
        int numOfRowsInTable = rows.getInt("count");
        for (int i = 0; i < numOfRowsInTable; i++) {
            ArrayList<Map<FieldDTO, Object>> row = new ArrayList<>();
            for (FieldDTO field : table.getFields()) {
                Map<FieldDTO, Object> column = new HashMap<>();
                ResultSet value = stmt.executeQuery("");
                
                row.add(column);
            }
        }
        return null;
    }
}
