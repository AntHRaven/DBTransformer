package readers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import readers.abstractions.AbstractRelationalDBReader;

public class PostgresDBReader extends AbstractRelationalDBReader {

    // constructor from super class !
    public PostgresDBReader(Connection connection) {
        super(connection);
    }

    // realisation of methods, which weren't realised in super class

    // these methods allow us to read user's db
    // and get all what we need to transform db to another one

    // these methods we will use in transformer (from postgre to another db)

    @Override
    public ArrayList<String> getAllTablesNames() {
        ArrayList<String> tables = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tablesMD = metaData.getTables(
                null,
                null,
                "%",
                new String[] { "TABLE" }
            );
            while (tablesMD.next()) {
                tables.add(tablesMD.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tables;
    }

    @Override
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

    @Override
    public ArrayList<String> getPrimaryKeyColumnsNames(String tableName) {
        return null;
    }

    @Override
    public ArrayList<String> getForeignKeyColumnsNames(String tableName) {
        return null;
    }
}
