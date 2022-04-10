import com.mongodb.*;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

public class Main {
    
    public static void main(String[] args) throws SQLException, UnknownHostException {
//        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
//        Connection postgresqlConnection = DriverManager.getConnection("", "", "");
//
//        Database from = new MongoDB(mongoClient);
//        Database to = new PostgreSQL(postgresqlConnection);
//
//        DBTManager.transform(from, to);
    
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB("DBName");
        DBCollection collection = database.getCollection("CollName");
        List<Integer> books = Arrays.asList(27464, 747854);
        DBObject person = new BasicDBObject("_id", new BasicDBObject("bla", new BasicDBObject("kek", "kuk")))
              .append("name", "Jo Bloggs");
        
        //collection.insert(person);
    
        //System.out.println(collection);
    
        DBCursor cursor = collection.find();
        
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            System.out.println(doc);
            //System.out.println(generateDocumentName(doc, "DOCUMENT"));
        }
        
    }
  
}
