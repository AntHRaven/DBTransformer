package transformer.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.Database;
import database.MongoDB;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import org.bson.Document;
import transformer.DBTransformer;

public class ToMongoDBTransformer implements DBTransformer {
    
    private DatabaseDTO databaseDTO;
    private MongoClient mongoClientTo;
    private MongoClient mongoClientFrom;
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        if (!(to instanceof MongoDB)) {return;}
        
        databaseDTO = from.makeDTO();
    
        // TODO: 05.05.2022 no mongo for both
        // do as in ToPostgreSQLTransformer
        mongoClientTo = ((MongoDB) to).getMongoClient();
        mongoClientFrom = ((MongoDB) from).getMongoClient();
        
        createAllDocuments();
    }
    
    @Override
    public void fillAllData() {
    
    }
    
    private void createDocument(TableDTO table){
        String DBName = mongoClientTo.listDatabaseNames().first();
        MongoDatabase db = mongoClientTo.getDatabase(DBName);
        
        db.createCollection(table.getName());
        MongoCollection<Document> collection = db.getCollection(table.getName());
        
        List<Document> documents = new ArrayList<>();
        
        for (FieldDTO field : table.getFields()) {
            if (field.isPK()) {
                if (field.getFK() == null) {
                    Map<String, Object> fields = new HashMap<>();
                    documents.add(new Document());
                } else {
                
                }
            } else {
                if (field.getFK() == null){
                
                
                } else {
                
                }
            }
        }
        collection.insertMany(documents);
        
    }
    
    private void createAllDocuments() {
        for (TableDTO table : databaseDTO.getTables()) {
            createDocument(table);
        }
 
    }
    
}
