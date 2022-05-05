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

public class DBMergerImpl
      implements DBMerger {
    
    @Override
    public DatabaseDTO getMergedDto(List<DatabaseDTO> databaseList) throws SQLException {
        List<TableDTO> tableDTOS = new ArrayList<>();
        
        for (DatabaseDTO databaseDTO : databaseList) {
            tableDTOS.addAll(databaseDTO.getTables());
        }
        
        Set<String> allUniqueTableNames = tableDTOS
              .stream()
              .map(TableDTO::getName)
              .collect(Collectors.toSet());
    
        DatabaseDTO databaseDTO = new DatabaseDTO();
        Set<TableDTO> tables = new HashSet<>();
        
        for (String tableName : allUniqueTableNames) {
            if (getTablesByName(tableDTOS, tableName).size() > 1) {
                tables.add(joiningTables(getTablesByName(tableDTOS, tableName), tableName));x
            }
            else {
                tables.addAll(getTablesByName(tableDTOS, tableName));
            }
        }
        
        databaseDTO.setTables(tables);
        return databaseDTO;
        
    }
    
    @Override
    public void merge(DatabaseDTO from, Database to) throws SQLException {
        to.getTransformer().transform(from, to);
    }
    
    private List<TableDTO> getTablesByName(List<TableDTO> tableDTOS, String name) {
        return tableDTOS.stream().filter((item) -> item.getName().equals(name)).collect(Collectors.toList());
    }
    
    private TableDTO joiningTables(List<TableDTO> tableDTOS, String tableName) {
        TableDTO table = new TableDTO();
        Set<FieldDTO> fieldDTOS = new HashSet<>();
        
        for (TableDTO tableDTO : tableDTOS) {
            fieldDTOS.addAll(tableDTO.getFields());
        }
        
        table.setName(tableName);
        table.setFields(new ArrayList<>(fieldDTOS));
        return table;
    }
    
}
