package data.provider;

import com.mongodb.MongoClient;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;

import java.util.ArrayList;
import java.util.Map;

public class MongoDBDataProvider extends Provider {
    
    private MongoClient mongoClient;
    
    public MongoDBDataProvider(MongoClient mongoClient, DatabaseDTO databaseDTO){
        super(databaseDTO);
        this.mongoClient = mongoClient;
    }
    
    @Override
    public ArrayList<ArrayList<Map<FieldDTO, Object>>> getTableRows(TableDTO table) {
        return null;
    }
}
