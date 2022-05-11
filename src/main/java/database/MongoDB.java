package database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.ToMongoDBTypeConverter;
import converter.types.FieldDTOMongoDBTypes;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import lombok.Getter;
import org.bson.Document;
import transformer.DBTransformer;
import transformer.impl.ToMongoDBTransformer;
import java.util.*;

public class MongoDB extends Database {
    
    @Getter
    private final MongoClient mongoClient;
    private final Set<TableDTO> tables = new HashSet<>();
    
    private static final String collectionTableName = "collections";
    private static final String collectionFieldName = "collection_name";
    private static final String documentIdFieldName = "_id";
    private static final String documentDefName = "document";
    private static final String delimiter = "_";
    
    public MongoDB(String dbName, MongoClient mongoClient, List<String> documentsNames) {
        super(dbName, documentsNames);
        this.mongoClient = mongoClient;
        this.dbTransformer = new ToMongoDBTransformer();
    }
    
    @Override
    public DBTransformer getTransformer() {
        return this.dbTransformer;
    }
    
    @Override
    public DatabaseDTO makeDTO() {
        makeAllTables();
        DatabaseDTO databaseDTO = new DatabaseDTO(this.name, tables, this.getClass());
        databaseDTO.initializeProvider();
        return databaseDTO;
    }
    
    private void makeAllTables(){
        makeTableCollections();
        for (MongoCollection<Document> collection : getAllCollections()) {
            for (Document doc : collection.find()) {
                makeTableFromDocument(doc, collection);
            }
        }
    }
    
    private void makeTableCollections(){
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO(collectionFieldName, FieldDTOMongoDBTypes.STRING, true, null));
        tables.add(new TableDTO(collectionTableName, fields));
    }
    
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
    
    private void makeTableFromSubObject(DBObject subObject, String tableName){
    
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO(documentIdFieldName, FieldDTOMongoDBTypes.OBJECT_ID, true, null));
        
        for (String key : subObject.keySet()) {
            String relTableName = tableName + key;
            Object field = subObject.get(key);
            
            //if it's object
            if (field instanceof DBObject) {
                fields.add(
                      new FieldDTO(key + documentIdFieldName, FieldDTOMongoDBTypes.STRING, true,
                                   new ForeignKeyDTO(relTableName, documentIdFieldName)));
    
                makeTableFromSubObject((DBObject) field, relTableName);
                
            //if not object
            }else {
                fields.add(new FieldDTO(key, ToMongoDBTypeConverter.getTypeWithClass(field.getClass()), false, null));
            }
        }
        
        tables.add(new TableDTO(tableName, fields));
    }
    
    private void makeTableFromDocument(Document document, MongoCollection<Document> collection){
        
        String name = generateDocumentName(document, collection.getNamespace().getCollectionName());
        ArrayList<FieldDTO> fields = new ArrayList<>();
        
        fields.add(
              new FieldDTO(collectionFieldName, FieldDTOMongoDBTypes.STRING, false,
              new ForeignKeyDTO(collectionTableName, collectionFieldName)));
        
        for (String key : document.keySet()) {
            Object field = document.get(key);
            boolean isPK = key.equals(documentIdFieldName);
            
            //if it's object
            if (field instanceof DBObject) {
                String subObjectName = name + delimiter + key;
                fields.add(
                      new FieldDTO(key + documentIdFieldName, FieldDTOMongoDBTypes.OBJECT_ID, isPK,
                                   new ForeignKeyDTO(subObjectName, documentIdFieldName)));
                makeTableFromSubObject((DBObject) field, subObjectName);
            //if not object
            } else {
               fields.add(new FieldDTO(key, ToMongoDBTypeConverter.getTypeWithClass(field.getClass()), isPK, null));
            }
        }
        tables.add(new TableDTO(name, fields));
    }
    
    private Set<MongoCollection<Document>> getAllCollections(){
        Set<MongoCollection<Document>> collections = new HashSet<>();
        MongoDatabase db = mongoClient.getDatabase(this.name);
        for (String collectionName :  db.listCollectionNames()) {
            if (names.contains(collectionName)) {
                collections.add(db.getCollection(collectionName));
            }
        }
        
        return collections;
    }
    
}
