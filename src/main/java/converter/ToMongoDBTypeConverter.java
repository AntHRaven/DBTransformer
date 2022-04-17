package converter;
import converter.types.MongoTypes;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import java.sql.Types;

public class ToMongoDBTypeConverter {
    
    private void convertAllFields(DatabaseDTO databaseDTO){
        for (TableDTO table : databaseDTO.getTables()) {
            for (FieldDTO field : table.getFields()) {
                //изменить поле
            }
        }
        
    }
    
//    private MongoTypes convert(Types type){
//
//    }
    
    private ToMongoDBTypeConverter(){}

}
