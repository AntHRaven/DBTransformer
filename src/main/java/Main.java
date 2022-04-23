import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.Database;
import database.MongoDB;
import com.mongodb.*;
import org.bson.Document;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {
    
    public static void main(String[] args) throws SQLException, UnknownHostException {
        //MongoClient mongoClient = new MongoClient("");
        //Connection postgresqlConnection = DriverManager.getConnection("", "", "");
//        MongoClient mongoClient = new MongoClient(new MongoClientURI("mon_idgodb://localhost:27017"));
//        Connection postgresqlConnection = DriverManager.getConnection("", "", "");
//
//        Database from = new MongoDB(mongoClient);
//        Database to = new PostgreSQL(postgresqlConnection);
//
//        DBTManager.transform(from, to);
        
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("DBName");
        MongoCollection<Document> collection = database.getCollection("CollName");
        Set< String> books = new HashSet<>();
        
        Date date = new Date(777);
        String date2 = "1985.06.28";
        books.add("kkk");
        Document person = new Document("_id", date2)
              .append("name", 25.8)
                    .append("books", books);
        
        //collection.insertOne(person);
        
        //System.out.println(collection);
    
    
        MongoClient mongoClient2 = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database2 = mongoClient2.getDatabase("DBName");
        MongoCollection<Document> collection2 = database.getCollection("CollName");
        
        //Database from = new MongoDB(mongoClient);
        //Database to = new PostgreSQL(postgresqlConnection);
        for (Document doc : collection2.find()) {
            ArrayList<Class> types = new ArrayList<>();
    
            for (String key : doc.keySet()) {
                System.out.println(doc.get(key).getClass());
            }
//
//            doc.values().stream().map(Object::getClass).collect(types);
//            System.out.println(types);
            System.out.println("\n");
            //System.out.println(generateDocumentName(doc, "DOCUMENT"));
        }
        
        
        
        
//        DBTManager DBTManager = new DBTManager();
//        DBTManager.transform(from, to);
    }
    
}
