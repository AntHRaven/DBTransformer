package database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.types.MongoTypes;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import org.bson.Document;
import transformer.DBTransformer;
import transformer.impl.ToMongoDBTransformer;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

public class MongoDB extends Database {
    
    private final MongoClient mongoClient;
    private  final Set<TableDTO> tables = new HashSet<>();
    
    private static final String collectionTableName = "collections";
    private static final String collectionFieldName = "collection_name";
    private static final String documentIdFieldName = "_id";
    private static final String documentDefName = "document";
    private static final String delimiter = "_";
    
    public MongoDB(MongoClient mongoClient) throws SQLException, UnknownHostException {
        this.mongoClient = mongoClient;
        this.dbTransformer = new ToMongoDBTransformer();
    }
    
    public MongoClient getMongoClient() {
        return mongoClient;
    }
    
    @Override
    public DBTransformer getTransformer() {
        return this.dbTransformer;
    }
    
    @Override
    public DatabaseDTO makeDTO() throws SQLException {
        makeAllTables();
        return new DatabaseDTO(tables);
    }
    
    private void makeAllTables(){
        makeTableCollections();
        for (MongoCollection<Document> collection : getAllCollections()) {
            for (Document doc : collection.find()) {
                makeTableFromDocument(doc);
            }
        }
    }
    
    private void makeTableCollections(){
        ArrayList<FieldDTO> fields = new ArrayList<>();
        //fields.add(new FieldDTO(collectionFieldName, MongoTypes.STRING, true, null));
        tables.add(new TableDTO(collectionTableName, fields));
    }
    
    public static String generateDocumentName(Document document){
        String name = documentDefName + delimiter;
        if (document.get(documentIdFieldName) instanceof DBObject){
            name += document.get(documentIdFieldName).toString();
            name = parseJson(name);
        }else {
            name = delimiter + document.get(documentIdFieldName);
        }
        return name;
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
        //fields.add(new FieldDTO(documentIdFieldName, MongoTypes.OBJECT_ID, true, null));
        
        for (String key : subObject.keySet()) {
            String relTableName = tableName + key;
            Object field = subObject.get(key);
            
            //if it's object
            if (field instanceof DBObject) {
//                fields.add(
//                      new FieldDTO(key + documentIdFieldName, MongoTypes.OBJECT_ID, true,
//                      new ForeignKeyDTO(relTableName, documentIdFieldName)));
    
                makeTableFromSubObject((DBObject) field, relTableName);
                
            //if not object
            }else {
                
                fields.add(new FieldDTO(key, 1, false, null));
            }
        }
        
        tables.add(new TableDTO(tableName, fields));
    }
    
    private void makeTableFromDocument(Document document){
        
        String name = generateDocumentName(document);
        ArrayList<FieldDTO> fields = new ArrayList<>();
        
//        fields.add(
//              new FieldDTO(collectionFieldName, MongoTypes.STRING, false,
//              new ForeignKeyDTO(collectionTableName, collectionFieldName)));
        
        for (String key : document.keySet()) {
            String subObjectName = name + delimiter + key;
            Object field = document.get(key);
            
            //if it's object
            if (field instanceof DBObject) {
                if (key.equals(documentIdFieldName)){
                    fields.add(
                          new FieldDTO(key, 1, true,
                          new ForeignKeyDTO(subObjectName, documentIdFieldName)));
                }else {
                    fields.add(
                          new FieldDTO(key + documentIdFieldName, 1, true,
                          new ForeignKeyDTO(subObjectName, documentIdFieldName)));
                }
                makeTableFromSubObject((DBObject) field, subObjectName);
            //if not object
            }else fields.add(new FieldDTO(key, 1, false, null));
        }
        tables.add(new TableDTO(name, fields));
    }
    
    private List<MongoDatabase> getAllDB(){
        List<MongoDatabase> databases = new ArrayList<>();
        for (String dbName : mongoClient.listDatabaseNames()) {
            MongoDatabase db = mongoClient.getDatabase(dbName);
            databases.add(db);
        }
        return databases;
    }
    
    private Set<MongoCollection<Document>> getAllCollections(){
        Set<MongoCollection<Document>> collections = new HashSet<>();
        for (MongoDatabase db : getAllDB()) {
            for (String collectionName :  db.listCollectionNames()) {
                collections.add(db.getCollection(collectionName));
            }
        }
        return collections;
    }
    
}
