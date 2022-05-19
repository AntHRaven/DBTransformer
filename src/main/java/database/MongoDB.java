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

import static data.provider.MongoDBStringConstantsProvider.*;

public class MongoDB extends Database {
    
    @Getter
    private final MongoClient mongoClient;
    private final Set<TableDTO> tables = new HashSet<>();
    
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
            if (!isIdenticalDocumentFields(collection)) {
                for (Document doc : collection.find()) {
                    makeTableFromDocument(doc, collection);
                }
            } else {
                makeTableFromCollection(collection);
            }
        }
    }
    
    private boolean isIdenticalDocumentFields(MongoCollection<Document> collection){
        List<String> keys = new ArrayList<>(Objects.requireNonNull(collection.find().first()).keySet());
        for (Document doc : collection.find()) {
            List<String> docFields = new ArrayList<>(doc.keySet());
            for (int i = 0; i < keys.size(); i++){
                if (!keys.get(i).equals(docFields.get(i))){
                        return false;
                }
            }
        }
        return true;
    }
    
    private void makeTableFromCollection(MongoCollection<Document> collection){
        String name = collection.getNamespace().getCollectionName();
        ArrayList<FieldDTO> fields = new ArrayList<>();
        Document doc = collection.find().first();
    
        if (doc != null) {
            makeTable(doc, name, fields, delimiterForCollectionRootName);
        }
    }
    
    private void makeTableCollections(){
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO(collectionFieldName, FieldDTOMongoDBTypes.STRING, true, null));
        tables.add(new TableDTO(collectionTableName, fields));
    }
    
    private void addFieldDTO(ArrayList<FieldDTO> fields, String key, String relTableName, Object field, boolean isPK){
        System.out.println(field.getClass());
        // if object
        if (field instanceof Document) {
            fields.add(
                  new FieldDTO(key + documentIdFieldName, FieldDTOMongoDBTypes.OBJECT_ID, isPK,
                               new ForeignKeyDTO(relTableName, documentIdFieldName)));
            makeTableFromSubObject((Document) field, relTableName);
        
        // if not object
        }else {
            fields.add(new FieldDTO(key, ToMongoDBTypeConverter.getTypeWithClass(field.getClass()), isPK, null));
        }
    }
    
    private void makeTable(Document document, String name, ArrayList<FieldDTO> fields, String delimiter){
        for (String key : document.keySet()) {
            Object field = document.get(key);
            boolean isPK = key.equals(documentIdFieldName);
            String subObjectName = name + delimiter + key;
            
            addFieldDTO(fields, key, subObjectName, field, isPK);
        }
        tables.add(new TableDTO(name, fields));
    }
    
    private void makeTableFromDocument(Document document, MongoCollection<Document> collection){
        
        String name = generateDocumentName(document, collection.getNamespace().getCollectionName());
        ArrayList<FieldDTO> fields = new ArrayList<>();
        
        fields.add(
              new FieldDTO(collectionFieldName, FieldDTOMongoDBTypes.STRING, false,
              new ForeignKeyDTO(collectionTableName, collectionFieldName)));
    
        makeTable(document, name, fields, delimiterForDocumentRootName);
    }
    
    private void makeTableFromSubObject(Document subObject, String tableName){
        
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO(documentIdFieldName, FieldDTOMongoDBTypes.OBJECT_ID, true, null));
        
        for (String key : subObject.keySet()) {
            String relTableName = tableName + key;
            Object field = subObject.get(key);
            addFieldDTO(fields, key, relTableName, field, false);
        }
        
        tables.add(new TableDTO(tableName, fields));
    }
    
    private Set<MongoCollection<Document>> getAllCollections(){
        Set<MongoCollection<Document>> collections = new HashSet<>();
        MongoDatabase db = mongoClient.getDatabase(this.name);
        for (String collectionName :  db.listCollectionNames()) {
            if (names.contains(collectionName) || names.isEmpty()) {
                collections.add(db.getCollection(collectionName));
            }
        }
        return collections;
    }
    
}
