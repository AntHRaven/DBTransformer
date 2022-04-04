package database;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.ForeignKeyDTO;
import dto.TableDTO;
import transformer.DBTransformer;
import transformer.ToMongoDBTransformer;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MongoDB
      extends Database {
    
    private final MongoClient mongoClient;
    
    public MongoDB(MongoClient mongoClient) throws SQLException, UnknownHostException {
        this.mongoClient = mongoClient;
        this.dbTransformer = new ToMongoDBTransformer();
    }
    
    @Override
    public DBTransformer getTransformer() {
        return this.dbTransformer;
    }
    
    @Override
    public DatabaseDTO makeDTO() throws SQLException {
        return new DatabaseDTO(getAllTables());
    }
    
    @Override
    protected ArrayList<TableDTO> getAllTables() throws SQLException {
        return null;
    }
    
    @Override
    protected ArrayList<FieldDTO> getAllTableFields(String tableName) throws SQLException {
        return null;
    }
    
    @Override
    protected ForeignKeyDTO getFK(String columnName, String tableName) throws SQLException {
        return null;
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
    
    private DBCursor getDocumentsCursor(DBCollection collection){
        return collection.find();
    }
    
}
