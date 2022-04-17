package converter;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;

public class ToPostgreSQLTypeConverter {
    
    private void convertAllFields(DatabaseDTO databaseDTO){
        for (TableDTO table : databaseDTO.getTables()) {
            for (FieldDTO field : table.getFields()) {
                //изменить поле
            }
        }
        
    }
    
    private ToPostgreSQLTypeConverter(){
    
    }

}
