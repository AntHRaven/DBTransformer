package data.provider;

import com.mongodb.DBObject;
import org.bson.Document;

public class MongoDBStringConstantsProvider {
    public static final String collectionTableName = "collections";
    public static final String collectionFieldName = "collection_name";
    public static final String documentIdFieldName = "_id";
    public static final String documentDefName = "document";
    public static final String delimiterForNames = "_";
    
    public static String getDocumentStringId(Document document){
        String id;
        id = document.get(documentIdFieldName).toString();
        if (document.get(documentIdFieldName) instanceof DBObject) {
            id = parseJson(id);
        }
        return id;
    }
    
    private static String parseJson(String s){
        return s.replace("{", "")
              .replace("}", "")
              .replace(":", "")
              .replaceAll("\"", "")
              .replaceAll("( )+", "_");
    }
    
    private MongoDBStringConstantsProvider(){}
}
