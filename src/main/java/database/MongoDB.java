package database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.ToMongoDBTypeConverter;
import converter.types.FieldDTOMongoDBTypes;
import data.NameType;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import lombok.Getter;
import org.bson.Document;
import transformer.DBTransformer;
import transformer.impl.ToMongoDBTransformer;
import java.util.*;
import static data.provider.FormatDataProvider.getNameFromMap;
import static data.provider.MongoDBStringConstantsProvider.*;

@Getter
public class MongoDB extends Database {
    
    private final MongoClient mongoClient;
    private final Set<TableDTO> tables = new HashSet<>();
    private final List<Map<NameType, List<String>>> objectNames = new ArrayList<>();
    
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
    
    // check if all fields in all documents of current collection are the same
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
        Map<NameType, List<String>> name = new HashMap<>();
        objectNames.add(name);
        
        List<String> relations = new ArrayList<>();
        
        name.put(NameType.COLLECTION, new ArrayList<> (List.of(collection.getNamespace().getCollectionName())));
        name.put(NameType.RELATION, relations);
        
        ArrayList<FieldDTO> fields = new ArrayList<>();
        Document doc = collection.find().first();
    
        if (doc != null) {
            makeTable(doc, name, fields);
        }
    }
    
    private void makeTableCollections(){
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO(collectionFieldName, FieldDTOMongoDBTypes.STRING, true, null));
        tables.add(new TableDTO(collectionTableName, fields));
    }
    
    private void addFieldDTO(ArrayList<FieldDTO> fields, String key, Map<NameType, List<String>> relTableName, Object field, boolean isPK){
        // if object
        if (field instanceof Document) {
            Map<NameType, List<String>> currentName = new HashMap<>();
            List<String> relations = new ArrayList<>();
            
            String name = getNameFromMap(relTableName) + delimiterForNames + key;
            
            currentName.put(NameType.SUB_OBJECT, new ArrayList<> (List.of(name)));
            currentName.put(NameType.RELATION, relations);
            relTableName.get(NameType.RELATION).add(name);
            
            fields.add(
                  new FieldDTO(key + documentIdFieldName, FieldDTOMongoDBTypes.OBJECT_ID, isPK,
                               new ForeignKeyDTO(name, documentIdFieldName)));
            makeTableFromSubObject((Document) field, name, currentName);
        
        // if not object
        }else {
            fields.add(new FieldDTO(key, ToMongoDBTypeConverter.getTypeWithClass(field.getClass()), isPK, null));
        }
    }
    
    private void makeTable(Document document, Map<NameType, List<String>> name, ArrayList<FieldDTO> fields){
        for (String key : document.keySet()) {
            Object field = document.get(key);
            boolean isPK = key.equals(documentIdFieldName);
    
            addFieldDTO(fields, key, name, field, isPK);
        }
        tables.add(new TableDTO(getNameFromMap(name), fields));
    }
    
    private void makeTableFromDocument(Document document, MongoCollection<Document> collection){
        
        Map<NameType, List<String>> name = new HashMap<>();
        objectNames.add(name);
        
        List<String> relations = new ArrayList<>();
        name.put(NameType.DOCUMENT, new ArrayList<>(List.of(documentDefName)));
        name.put(NameType.ID, new ArrayList<>(List.of(getDocumentStringId(document))));
        name.put(NameType.COLLECTION, new ArrayList<>(List.of(collection.getNamespace().getCollectionName())));
        name.put(NameType.RELATION, relations);
        
        ArrayList<FieldDTO> fields = new ArrayList<>();
        
        fields.add(
              new FieldDTO(collectionFieldName, FieldDTOMongoDBTypes.STRING, false,
              new ForeignKeyDTO(collectionTableName, collectionFieldName)));
    
        makeTable(document, name, fields);
    }
    
    private void makeTableFromSubObject(Document subObject, String tableName, Map<NameType, List<String>> currentName){
        
        ArrayList<FieldDTO> fields = new ArrayList<>();
        fields.add(new FieldDTO(documentIdFieldName, FieldDTOMongoDBTypes.OBJECT_ID, true, null));
        
        for (String key : subObject.keySet()) {
            
            Object field = subObject.get(key);
            addFieldDTO(fields, key, currentName, field, false);
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
