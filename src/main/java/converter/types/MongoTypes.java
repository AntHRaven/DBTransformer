package converter.types;

import java.sql.Types;

public enum MongoTypes {
    
    STRING("String"),
    ARRAY("ArrayList"),
    BINARY_DATA("binary data"),
    BOOLEAN("boolean"),
    DATE("date"),
    DOUBLE("double"),
    INTEGER("integer"),
    LONG("long"),
    JAVASCRIPT("javascript"),
    MIN_KEY("min key"),
    MAX_KEY("max key"),
    NULL("null"),
    OBJECT("object"),
    OBJECT_ID("objectId"),
    REGULAR_EXPRESSION("regular expression"),
    DECIMAL_128("decimal 128"),
    TIMESTAMP("timestamp");
    
    MongoTypes(String string) {}
    
    private Types convertToPostgreSQLType(){
        return null;
    }
}
