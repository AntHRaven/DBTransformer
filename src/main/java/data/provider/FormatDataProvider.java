package data.provider;

import data.NameType;
import dto.FieldDTO;
import java.util.List;
import java.util.Map;

import static data.provider.MongoDBStringConstantsProvider.delimiterForNames;

public class FormatDataProvider {
    
    public static String getListOfOldFieldsNames(Map<String, FieldDTO> fields){
        StringBuilder list = new StringBuilder();
        for (String key : fields.keySet()) {
            list.append(key).append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    public static String getListOfNewFieldsNames(Map<String, FieldDTO> fields){
        StringBuilder list = new StringBuilder();
        for (String key : fields.keySet()) {
            list.append(fields.get(key).getName()).append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    public static String getListOfValues(List<String> values) {
        StringBuilder list = new StringBuilder();
        for (String val : values) {
            list.append("'").append(val).append("'").append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    public static String getListOfFields(List<String> fields) {
        StringBuilder list = new StringBuilder();
        for (String val : fields) {
            list.append(val).append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    public static String getNameFromMap(Map<NameType, List<String>> map){
        
        StringBuilder name = new StringBuilder();
    
        if (map.containsKey(NameType.DOCUMENT)) {
            name.append(map.get(NameType.COLLECTION).get(0))
                  .append(delimiterForNames)
                  .append(map.get(NameType.DOCUMENT).get(0))
                  .append(delimiterForNames)
                  .append(map.get(NameType.ID).get(0));
        } else if (map.containsKey(NameType.SUB_OBJECT))  {
            name.append(map.get(NameType.SUB_OBJECT).get(0));
        } else {
            name.append(map.get(NameType.COLLECTION).get(0));
        }
        
        return name.toString();
    }
}
