package transformer;

import java.sql.SQLException;

import com.mongodb.MongoClient;
import database.Database;
import database.MongoDB;
import dto.DatabaseDTO;

public class ToMongoDBTransformer implements DBTransformer {
    
    private DatabaseDTO databaseDTO;
    
    private void makeTables(MongoClient mongoClient){
    
    }
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        if (!(to instanceof MongoDB)) return;
        
        databaseDTO = from.makeDTO();
        MongoClient mongoClient = ((MongoDB) to).getMongoClient();
        
        makeTables(mongoClient);
    }
}
