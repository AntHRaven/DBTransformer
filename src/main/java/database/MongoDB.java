package database;

import com.mongodb.*;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import transformer.DBTransformer;
import transformer.ToMongoDBTransformer;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

public class MongoDB extends Database {
    
    // TODO: types
    
    private final MongoClient mongoClient;
    private final Set<TableDTO> tables = new HashSet<>();
    
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
        for (DBCollection collection : getAllCollections()) {
            
            DBCursor cursor = collection.find();
            
            if(cursor.hasNext()) {
                DBObject document = cursor.next();
                makeTableFromDocument(document, collection);
            }
            
            makeTableFromCollection(collection);
        }
    }
    
    private void makeTableFromCollection(DBCollection collection){
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO("_id", 4, true, null));
        tables.add(new TableDTO(collection.getName(), fields));
    }
    
    static String generateDocumentName(DBObject document, String tableName){
        StringBuilder name = new StringBuilder(tableName);
        if (document.get("_id") instanceof DBObject){
            
            name.append(document.get("_id").toString()
                              .replace("{", "")
                              .replace("}", "")
                              .replace(":", "")
                              .replaceAll("\"", "")
                              .replaceAll("( )+", "_"));
            
        }else {
            name.append("_").append(document.get("_id"));
        }
        return name.toString();
    }
    
    private void makeTableFromSubObject(DBObject subObject, String tableName){
    
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO("_id", 1, true, null));
        
        for (String key : subObject.keySet()) {
            Object field = subObject.get(key);
            
            //if it's object
            if (field instanceof DBObject) {
                fields.add(
                      new FieldDTO(key + "_id", 1, true,
                      new ForeignKeyDTO(tableName + key, "_id")));
    
                makeTableFromSubObject((DBObject) field, tableName + key);
                
            //if not object
            }else fields.add(new FieldDTO(key, 1, false, null));
        }
        
        tables.add(new TableDTO(tableName, fields));
    }
    
    private void makeTableFromDocument(DBObject document, DBCollection collection){
        
        String name = generateDocumentName(document, "DOCUMENT");
        ArrayList<FieldDTO> fields = new ArrayList<>();
        for (String key : document.keySet()) {
            String subObjectName = name + "_" + key;
            Object field = document.get(key);
    
            if (key.equals("_id")) {
                //if key is _id and it's object
                // TODO: 10.04.2022
                //PROBLEM -- we need two foreign keys on one field...
                //can we generate another additional field ?
                if (field instanceof DBObject) {
                    fields.add(
                          new FieldDTO(key + "_id", 1, true,
                          new ForeignKeyDTO(subObjectName, "_id")));
                    makeTableFromSubObject((DBObject) field, subObjectName);
                }
                
                //if key is _id and not object
                else fields.add(
                      new FieldDTO(key, 1, true,
                      new ForeignKeyDTO(collection.getName(), "_id")));
            }
            else {
                //if not PK field and it's object
                if (field instanceof DBObject) {
                    fields.add(
                          new FieldDTO(key + "_id", 1, true,
                          new ForeignKeyDTO(subObjectName, "_id")));
                    
                    makeTableFromSubObject((DBObject) field, subObjectName);
                    
                //if not PK field and not object
                }else fields.add(new FieldDTO(key, 1, false, null));
            }
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
