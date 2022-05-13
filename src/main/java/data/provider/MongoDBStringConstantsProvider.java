package data.provider;

import com.mongodb.DBObject;
import org.bson.Document;

public class MongoDBStringConstantsProvider {
    public static final String collectionTableName = "collections";
    public static final String collectionFieldName = "collection_name";
    public static final String documentIdFieldName = "_id";
    public static final String documentDefName = "document";
    public static final String delimiter = "_";
    
    public static String generateDocumentName(Document document, String collectionName){
        String name = collectionName + delimiter + documentDefName + delimiter;
        name += format(document.get(documentIdFieldName).toString());
        if (document.get(documentIdFieldName) instanceof DBObject) {
            name = parseJson(name);
        }
        return name;
    }
    
    private static String format( String s){
        return s.replace("_", "");
    }
    
    private static String parseJson(String s){
        return s.replace("{", "")
              .replace("}", "")
              .replace(":", "")
              .replaceAll("\"", "")
              .replaceAll("( )+", delimiter);
    }
    
    private MongoDBStringConstantsProvider(){}
}
