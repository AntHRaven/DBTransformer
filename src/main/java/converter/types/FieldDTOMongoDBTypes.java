package converter.types;

import org.bson.types.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public enum FieldDTOMongoDBTypes implements FieldDTOTypes {
    
    ARRAY(ArrayList.class),
    STRING(String.class),
    BINARY_DATA(Binary.class),
    BOOLEAN(Boolean.class),
    DATE(Date.class),
    DOUBLE(Double.class),
    INTEGER(Integer.class),
    LONG(Long.class),
    NULL(null),
    OBJECT_ID(ObjectId.class),
    DECIMAL_128(Decimal128.class),
    TIMESTAMP(LocalDateTime.class);
    
    private final Class<?> typeClass;
    
    FieldDTOMongoDBTypes(Class<?> typeClass) {
        this.typeClass = typeClass;
    }
    
    public Class<?> getTypeClass() {
        return typeClass;
    }
}
