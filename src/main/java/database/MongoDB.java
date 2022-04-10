package database;

import com.mongodb.*;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import transformer.DBTransformer;
import transformer.impl.ToMongoDBTransformer;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

public class MongoDB extends Database {
    
    // TODO: types
    
    private final MongoClient mongoClient;
    private final Set<TableDTO> tables = new HashSet<>();
    private final String collectionTableName = "collections";
    private final String collectionFieldName = "collection_name";
    private final String documentIdFieldName = "_id";
    private final String documentDefName = "document";
    private final String delimiter = "_";
    
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
        for (DBCollection collection : getAllCollections()) {
            
            DBCursor cursor = collection.find();
            
            if(cursor.hasNext()) {
                DBObject document = cursor.next();
                makeTableFromDocument(document);
            }
        }
    }
    
    private void makeTableCollections(){
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO(collectionFieldName, 4, true, null));
        tables.add(new TableDTO(collectionTableName, fields));
    }
    
    private String generateDocumentName(DBObject document){
        String name = documentDefName + delimiter;
        if (document.get(documentIdFieldName) instanceof DBObject){
            name += document.get(documentIdFieldName).toString();
            name = parseJson(name);
        }else {
            name = delimiter + document.get(documentIdFieldName);
        }
        return name;
    }
    
    private String parseJson(String s){
        return s.replace("{", "")
              .replace("}", "")
              .replace(":", "")
              .replaceAll("\"", "")
              .replaceAll("( )+", delimiter);
    }
    
    private void makeTableFromSubObject(DBObject subObject, String tableName){
    
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO(documentIdFieldName, 1, true, null));
        
        for (String key : subObject.keySet()) {
            String relTableName = tableName + key;
            Object field = subObject.get(key);
            
            //if it's object
            if (field instanceof DBObject) {
                fields.add(
                      new FieldDTO(key + documentIdFieldName, 1, true,
                      new ForeignKeyDTO(relTableName, documentIdFieldName)));
    
                makeTableFromSubObject((DBObject) field, relTableName);
                
            //if not object
            }else fields.add(new FieldDTO(key, 1, false, null));
        }
        
        tables.add(new TableDTO(tableName, fields));
    }
    
    private void makeTableFromDocument(DBObject document){
        
        String name = generateDocumentName(document);
        ArrayList<FieldDTO> fields = new ArrayList<>();
        
        fields.add(
              new FieldDTO(collectionFieldName, 1, false,
              new ForeignKeyDTO(collectionTableName, collectionFieldName)));
        
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
    
    private List<DB> getAllDB(){
        List<DB> databases = new ArrayList<>();
        for (String dbName : mongoClient.getDatabaseNames()) {
            DB db = mongoClient.getDB(dbName);
            databases.add(db);
        }
        return databases;
    }
    
    private Set<DBCollection> getAllCollections(){
        Set<DBCollection> collections = new HashSet<>();
        for (DB db : getAllDB()) {
            Set<String> collectionsNames = db.getCollectionNames();
            for (String collectionName : collectionsNames) {
                collections.add(db.getCollection(collectionName));
            }
        }
        return collections;
    }
    
}
