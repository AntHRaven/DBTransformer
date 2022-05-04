package data.provider;

import data.TableData;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Provider {
    
    protected final DatabaseDTO databaseDTO;
    
    private Map<TableData, Map<String, FieldDTO>> databaseMetadata;
    
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
    
    public void updateTableName(String oldTableName, String newTableName){
        for (TableData tableData : databaseMetadata.keySet()) {
            if (tableData.getOldName().equals(oldTableName)){
                if (isUniqueTableName(newTableName)) {
                    tableData.getTableDTO().setName(newTableName);
                }
            }
        }
    }
    
    public void updateFieldName(String oldFieldName, String newFieldName, TableDTO tableDTO){
        for (TableData tableData : databaseMetadata.keySet()) {
            if (tableData.getTableDTO().equals(tableDTO)){
                if (isUniqueFieldName(newFieldName, tableDTO)) {
                    FieldDTO fieldDTO = databaseMetadata.get(tableData).get(oldFieldName);
                    fieldDTO.setName(newFieldName);
                }
            }
        }
    }
    
    public void deleteTable(String tableName){
        for (TableData table : databaseMetadata.keySet()) {
            if (table.getTableDTO().getName().equals(tableName)){
                databaseMetadata.remove(table);
            }
        }
    }
    
    public  void deleteField(String tableName, String fieldName){
        for (TableData table : databaseMetadata.keySet()) {
            if (table.getTableDTO().getName().equals(tableName)){
                databaseMetadata.get(table).remove(fieldName);
            }
        }
    }
}
