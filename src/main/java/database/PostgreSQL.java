package database;

import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import org.postgresql.ds.PGConnectionPoolDataSource;
import transformer.DBTransformer;
import transformer.impl.ToPostgresDBTransformer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class PostgreSQL
      extends Database {
    
    private final DatabaseMetaData metaData;
    private final PGConnectionPoolDataSource connectionPool;
    
    public PostgreSQL(PGConnectionPoolDataSource connectionPool) throws SQLException {
        this.dbTransformer = new ToPostgresDBTransformer();
        metaData = connectionPool.getConnection().getMetaData();
        this.connectionPool = connectionPool;
    }
    
    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }
    
    @Override
    public DBTransformer getTransformer() {
        return this.dbTransformer;
    }
    
    @Override
    public DatabaseDTO makeDTO() throws SQLException {
        return new DatabaseDTO(getAllTables(), connectionPool.getConnection().getMetaData().getURL());
    }
    
    protected Set<TableDTO> getAllTables() throws SQLException {
        Set<TableDTO> tables = new HashSet<>();
        for (String tableName : getAllTablesNames()) {
            TableDTO tableDTO = new TableDTO(tableName, getAllTableFields(tableName));
            tables.add(tableDTO);
        }
        return tables;
    }
    
    private ArrayList<String> getAllTablesNames() {
        ArrayList<String> tableNames = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connectionPool.getConnection().getMetaData();
            ResultSet tablesMD = metaData.getTables(
                  null,
                  null,
                  "%",
                  new String[]{"TABLE"});
            
            while (tablesMD.next()) {
                tableNames.add(tablesMD.getString("TABLE_NAME"));
            }
        }
        catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return tableNames;
    }
    
    protected ArrayList<FieldDTO> getAllTableFields(String tableName) throws SQLException {
        ArrayList<FieldDTO> fields = new ArrayList<>();
        Statement stmt = connectionPool.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + tableName);
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            String columnName = rsmd.getColumnName(i);
            String columnType = rsmd.getColumnTypeName(i);
            FieldDTO fieldDTO = new FieldDTO(columnName, columnType, isPrimary(columnName, tableName), getFK(columnName, tableName));
            fields.add(fieldDTO);
        }
        return fields;
    }
   
    private boolean isPrimary(String columnName, String tableName) throws SQLException {
        for (String key : getPK(tableName)) {
            if (columnName.equals(key)) {return true;}
        }
        return false;
    }
    private ArrayList<String> getPK(String tableName) throws SQLException {
        ArrayList<String> primaryKeys = new ArrayList<>();
        ResultSet rs = metaData.getPrimaryKeys(null, null, tableName);
        while (rs.next()) {
            primaryKeys.add(rs.getString(4));
        }
        return primaryKeys;
    }
    
    private ForeignKeyDTO getFK(String columnName, String tableName) throws SQLException {
        ResultSet rs = metaData.getImportedKeys(null, null, tableName);
        while (rs.next()) {
            if (columnName.equals(rs.getString("FKCOLUMN_NAME"))) {
                return new ForeignKeyDTO(rs.getString("PKTABLE_NAME"), rs.getString("PKCOLUMN_NAME"));
            }
        }
        return null;
    }
    
   
}
