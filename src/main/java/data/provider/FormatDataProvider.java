package data.provider;

import dto.FieldDTO;
import java.util.List;
import java.util.Map;

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
}
