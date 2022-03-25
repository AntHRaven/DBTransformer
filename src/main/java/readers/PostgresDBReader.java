package readers;

import readers.interfaces.DBReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostgresDBReader implements DBReader {
    
    public PostgresDBReader(Connection connection) {
    }
    
    public ArrayList<String> getAllTablesNames() {
//        ArrayList<String> tables = new ArrayList<>();
//        try {
//            DatabaseMetaData metaData = connection.getMetaData();
//            ResultSet tablesMD = metaData.getTables(
//                null,
//                null,
//                "%",
//                new String[] { "TABLE" }
//            );
//            while (tablesMD.next()) {
//                tables.add(tablesMD.getString("TABLE_NAME"));
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return tables;
        return null;
    }


    public Map<String, String> getAllFieldsNames(String tableName) {
        Map<String, String> fields = new HashMap<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmd = rs.getMetaData();
            for(int i = 1; i<=rsmd.getColumnCount(); i++) {
                fields.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return fields;
    }

   
    public ArrayList<String> getPrimaryKeyColumnsNames(String tableName) {
        return null;
    }

    public ArrayList<String> getForeignKeyColumnsNames(String tableName) {
        return null;
    }
    
    
}
