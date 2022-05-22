package merger.impl;

import database.Database;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import merger.DBMerger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DBMergerImpl implements DBMerger {

    @Override
    public DatabaseDTO getMergedDto(List<Database> databaseList, Database to) {
        List<TableDTO> tableDTOS = new ArrayList<>();
        List<DatabaseDTO> databaseDTOS = databaseList.stream().map((item) -> {
          try {
            return item.makeDTO();
          } catch (SQLException e) {
            e.printStackTrace();
          }
          return null;
        }).toList();
        Class<? extends Database> marker = databaseDTOS.iterator().next().getMarker();

        for (DatabaseDTO databaseDTO : databaseDTOS) {
            tableDTOS.addAll(databaseDTO.getTables());
        }

        Set<String> allUniqueTableNames = tableDTOS
              .stream()
              .map(TableDTO::getName)
              .collect(Collectors.toSet());

        Set<TableDTO> tables = new HashSet<>();

        for (String tableName : allUniqueTableNames) {
            if (getTablesByName(tableDTOS, tableName).size() > 1) {
                tables.add(joinTables(getTablesByName(tableDTOS, tableName), tableName));
            }
            else {
                tables.addAll(getTablesByName(tableDTOS, tableName));
            }
        }

        DatabaseDTO databaseDTO = new DatabaseDTO(to.getName(), tables, marker);
        databaseDTO.initializeProvider();
        return databaseDTO;
    }



    @Override
    public void merge(Database from, Database to) throws SQLException, InterruptedException {
        to.getTransformer().transform(from, to);
    }

    private List<TableDTO> getTablesByName(List<TableDTO> tableDTOS, String name) {
        return tableDTOS.stream().filter((item) -> item.getName().equals(name)).collect(Collectors.toList());
    }

    private TableDTO joinTables(List<TableDTO> tableDTOS, String tableName) {
        Set<FieldDTO> fieldDTOS = new HashSet<>();

        for (TableDTO tableDTO : tableDTOS) {
            fieldDTOS.addAll(tableDTO.getFields());
        }

        return new TableDTO(tableName, new ArrayList<>(fieldDTOS));
    }
}
