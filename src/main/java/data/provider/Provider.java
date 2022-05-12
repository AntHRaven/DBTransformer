package data.provider;

import data.TableData;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

// help us have access to data
// (keep old names, which are linked to current table/field dto)
// also have methods, that changing these names, deleting tables/fields
public class Provider {
    
    @Getter
    private final DatabaseDTO databaseDTO;
    
    @Getter
    private final Map<TableData, Map<String, FieldDTO>> databaseMetadata = new HashMap<>();
    
    public Provider(DatabaseDTO databaseDTO){
        this.databaseDTO = databaseDTO;
        for (TableDTO tableDTO : databaseDTO.getTables()) {
            Map<String, FieldDTO> fields = new HashMap<>();
            for (FieldDTO fieldDTO : tableDTO.getFields()) {
                fields.put(fieldDTO.getName(), fieldDTO);
            }
            databaseMetadata.put(new TableData(tableDTO.getName(), tableDTO), fields);
        }
    }
    
    private boolean isUniqueTableName(String name){
        for (TableDTO tableDTO : databaseDTO.getTables()) {
            if (tableDTO.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isUniqueFieldName(String name, TableDTO tableDTO){
        for (FieldDTO fieldDTO : tableDTO.getFields()) {
            if (fieldDTO.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }
    
    public DatabaseDTO updateTableName(String oldTableName, String newTableName){
        for (TableData tableData : databaseMetadata.keySet()) {
            if (tableData.getOldName().equals(oldTableName)){
                if (isUniqueTableName(newTableName)) {
                    tableData.getTableDTO().setName(newTableName);
                }
            }
        }
        return databaseDTO;
    }
    
    public DatabaseDTO updateFieldName(String oldFieldName, String newFieldName, TableDTO tableDTO){
        for (TableData tableData : databaseMetadata.keySet()) {
            if (tableData.getTableDTO().equals(tableDTO)){
                if (isUniqueFieldName(newFieldName, tableDTO)) {
                    FieldDTO fieldDTO = databaseMetadata.get(tableData).get(oldFieldName);
                    fieldDTO.setName(newFieldName);
                }
            }
        }
        return databaseDTO;
    }
    
    public DatabaseDTO deleteTable(String tableName){
        for (TableData table : databaseMetadata.keySet()) {
            if (table.getTableDTO().getName().equals(tableName)){
                databaseMetadata.remove(table);
            }
        }
        return databaseDTO;
    }
    
    public DatabaseDTO deleteField(String tableName, String fieldName){
        for (TableData table : databaseMetadata.keySet()) {
            if (table.getTableDTO().getName().equals(tableName)){
                databaseMetadata.get(table).remove(fieldName);
            }
        }
        return databaseDTO;
    }
}
