package readers.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.fieldTypes.PostgresTypes;
import models.FieldModel;
import readers.DBReader;

public class PostgresSQLDBReader implements DBReader {

  @Override
  public ArrayList<String> getAllTablesNames(Connection connection) {
    ArrayList<String> tables = new ArrayList<>();
    try {
      DatabaseMetaData metaData = connection.getMetaData();
      ResultSet tablesMD = metaData.getTables(
          null,
          null,
          "%",
          new String[]{"TABLE"}
      );
      while (tablesMD.next()) {
        tables.add(tablesMD.getString("TABLE_NAME"));
      }
    } catch (SQLException e) {

      System.out.println("ERROR: " + e.getMessage());
    }
    return tables;
  }

  @Override
  public List<FieldModel> getAllFields(Connection connection, String tableName) {
    List<FieldModel> fields = new ArrayList<>();
    try {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("select * from " + tableName);
      ResultSetMetaData rsmd = rs.getMetaData();
      for (int i = 1; i <= rsmd.getColumnCount(); i++) {
        fields.add(new FieldModel(rsmd.getColumnName(i), PostgresTypes.valueOfLabel(rsmd.getColumnTypeName(i)), false));
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
