package database;

import converter.ToPostgreSQLTypeConverter;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import org.postgresql.ds.PGConnectionPoolDataSource;
import transformer.DBTransformer;
import transformer.impl.ToPostgreSQLTransformer;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostgreSQL extends Database {

  private final DatabaseMetaData metaData;
  private final PGConnectionPoolDataSource connectionPool;

  // TODO: 05.05.2022
  // threads = for me (understand how all work)
  public PostgreSQL(String dbName, PGConnectionPoolDataSource connectionPool, List<String> tablesNames) throws SQLException {
    super(dbName, tablesNames);
    this.dbTransformer = new ToPostgreSQLTransformer();
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
    DatabaseDTO databaseDTO = new DatabaseDTO(this.name, getAllTables(), this.getClass());
    databaseDTO.initializeProvider();
    return databaseDTO;
  }

  private Set<TableDTO> getAllTables() throws SQLException {
    Set<TableDTO> tables = new HashSet<>();

    for (String tableName : getAllTablesNames()) {

      if (names.contains(tableName) || names.isEmpty()) {
        TableDTO tableDTO = new TableDTO(tableName, getAllTableFields(tableName));
        tables.add(tableDTO);
      }
    }
    return tables;
  }

  private ArrayList<FieldDTO> getAllTableFields(String tableName) throws SQLException {
    ArrayList<FieldDTO> fields = new ArrayList<>();
    ResultSet rs = metaData.getColumns(null, null, tableName, "%");

    while (rs.next()) {
      String columnName = rs.getString(4);
      int columnType = rs.getInt(5);
      FieldDTO fieldDTO = new FieldDTO(columnName, ToPostgreSQLTypeConverter.getTypeWithName(columnType), isPrimary(columnName, tableName),
          getFK(columnName, tableName));
      fields.add(fieldDTO);
    }
    return fields;
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

  private boolean isPrimary(String columnName, String tableName) throws SQLException {
    for (String key : getTablePrimaryKeys(tableName)) {
        if (columnName.equals(key)) {
            return true;
        }
    }
    return false;
  }

  public ArrayList<String> getAllTablesNames() throws SQLException {
    ArrayList<String> tablesNames = new ArrayList<>();
    ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});

    while (rs.next()) {
      tablesNames.add(rs.getString(3));
    }
    return tablesNames;
  }

  private ArrayList<String> getTablePrimaryKeys(String tableName) throws SQLException {
    ArrayList<String> primaryKeys = new ArrayList<>();
    ResultSet rs = metaData.getPrimaryKeys(null, null, tableName);

    while (rs.next()) {
      primaryKeys.add(rs.getString(4));
    }
    return primaryKeys;
  }


}

