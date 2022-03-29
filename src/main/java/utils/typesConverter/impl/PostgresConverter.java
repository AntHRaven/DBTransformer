package utils.typesConverter.impl;

import java.util.List;

import utils.fieldTypes.PostgresTypes;
import utils.typesConverter.TypesConverter;

public class PostgresConverter
      implements TypesConverter<PostgresTypes> {
    
    @Override
    public String convert(PostgresTypes typeName) {
        return null;
    }
    
    @Override
    public List<String> convertAll(List<PostgresTypes> typesNames) {
        return null;
    }
}
