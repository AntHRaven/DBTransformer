package transformer.impl;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToMongoDBTransformer
      implements DBTransformer {
    
    private DatabaseDTO databaseDTO;
    private MongoClient mongoClientTo;
    private MongoClient mongoClientFrom;
    
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        if (!(to instanceof MongoDB)) {return;}
        
        databaseDTO = from.makeDTO();
//        mongoClientTo = ((MongoDB) to).getMongoClient();
//        mongoClientFrom = ((MongoDB) from).getMongoClient();
        
        createAllDocuments();
    }
    
    @Override
    public void transform(DatabaseDTO from, Database to) throws SQLException {
    
    }
    
    private void createDocument(TableDTO table) {
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
                }
                else {
                
                }
            }
            else {
                if (field.getFK() == null) {
                
                
                }
                else {
                
                }
            }
        }
        collection.insertMany(documents);
        
    }
    
    private void createAllDocuments() {
        for (TableDTO table : databaseDTO.getTables()) {
            createDocument(table);
        }
        //из каждой таблицы сделать отдельный документ
        //ключ не трогать, пусть сам генерится
        //праймари будет просто отдельным полем
        
        /*
        таблица
        имя   возраст пк
        катя  14      1
        оля   19      2
        маша  2       3
        
        получаем коллекцию = с именем таблицы
        
        {_id = пк,
         имя = катя,
         возраст = 14
         }
         
         и тд
         
         если есть вложенная таблица
         то генерируем
         релфилд = {сюда поля релтаблицы, кроме _id}
         добавлять рекурсивно
        
         */
    }
    
}
