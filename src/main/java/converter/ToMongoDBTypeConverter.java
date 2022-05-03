package converter;
import converter.types.FieldDTOMongoDBTypes;
import converter.types.FieldDTOPostgreSQLTypes;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import org.bson.types.Binary;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class ToMongoDBTypeConverter {

//    public void convertAllFields(DatabaseDTO databaseDTO){
//        for (TableDTO table : databaseDTO.getTables()) {
//            for (FieldDTO field : table.getFields()) {
//                field.setType(convert((FieldDTOPostgreSQLTypes) field.getType()));
//            }
//        }
//
//    }
    
    private static FieldDTOMongoDBTypes convert(FieldDTOPostgreSQLTypes type){
        switch (type){
            case CHAR, VARCHAR, TEXT: {
                return FieldDTOMongoDBTypes.STRING;
            }
            case BYTEA:{
                return FieldDTOMongoDBTypes.BINARY_DATA;
            }
            case TIMESTAMP, TIMESTAMP_WITH_TIME_ZONE:{
                return FieldDTOMongoDBTypes.TIMESTAMP;
            }
            case INTERVAL:{
                return FieldDTOMongoDBTypes.ARRAY;
            }
            case SERIAL, SMALLSERIAL, BIGSERIAL:{
                return FieldDTOMongoDBTypes.OBJECT_ID;
            }
            case INTEGER, SMALLINT:{
                return FieldDTOMongoDBTypes.INTEGER;
            }
            case BIGINT:{
                return FieldDTOMongoDBTypes.LONG;
            }
            case NUMERIC, DECIMAL:{
                return FieldDTOMongoDBTypes.DECIMAL_128;
            }
            case REAL, DOUBLE_PRECISION:{
                return FieldDTOMongoDBTypes.DOUBLE;
            }
            default: return FieldDTOMongoDBTypes.STRING;
        }
    }
    
    public static <T> FieldDTOMongoDBTypes getTypeWithClass(Class<T> typeClass){
        if (ArrayList.class.equals(typeClass)) {
            return FieldDTOMongoDBTypes.ARRAY;
        } else if (Binary.class.equals(typeClass)){
            return FieldDTOMongoDBTypes.BINARY_DATA;
        } else if (Boolean.class.equals(typeClass)){
            return FieldDTOMongoDBTypes.BOOLEAN;
        } else if (Date.class.equals(typeClass)){
            return FieldDTOMongoDBTypes.DATE;
        } else if (Double.class.equals(typeClass)){
            return FieldDTOMongoDBTypes.DOUBLE;
        } else if (Integer.class.equals(typeClass)){
            return FieldDTOMongoDBTypes.INTEGER;
        } else if (Long.class.equals(typeClass)){
            return FieldDTOMongoDBTypes.LONG;
        } else if (typeClass == null){
            return FieldDTOMongoDBTypes.NULL;
        } else if (ObjectId.class.equals(typeClass)){
            return FieldDTOMongoDBTypes.OBJECT_ID;
        } else if (Decimal128.class.equals(typeClass)){
            return FieldDTOMongoDBTypes.DECIMAL_128;
        } else if (LocalDateTime.class.equals(typeClass)){
            return FieldDTOMongoDBTypes.TIMESTAMP;
        } else{
            return FieldDTOMongoDBTypes.STRING;
        }
    }
    
    private ToMongoDBTypeConverter(){}

}
