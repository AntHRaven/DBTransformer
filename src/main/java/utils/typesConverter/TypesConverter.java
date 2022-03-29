package utils.typesConverter;

import java.util.List;

import utils.fieldTypes.GenericTypes;

public interface TypesConverter<T extends GenericTypes> {
    String convert(T typeName);
    
    List<String> convertAll(List<T> typesNames);
}
