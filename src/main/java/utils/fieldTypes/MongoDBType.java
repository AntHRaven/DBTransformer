package utils.fieldTypes;

public enum MongoDBType
      implements GenericTypes {
    
    VARCHAR("varchar");
    
    private final String type;
    
    MongoDBType(String type) {
        this.type = type;
    }
    
    public static MongoDBType valueOfLabel(String label) {
        for (MongoDBType e : values()) {
            if (e.type.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
