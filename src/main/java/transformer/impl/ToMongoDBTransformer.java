package transformer.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.ToMongoDBTypeConverter;
import data.TableData;
import database.Database;
import database.MongoDB;
import database.PostgreSQL;
import dto.DatabaseDTO;
import dto.FieldDTO;
import org.bson.Document;
import transformer.DBTransformer;
import static data.provider.FormatDataProvider.*;

public class ToMongoDBTransformer implements DBTransformer {
    
    private DatabaseDTO databaseDTO;
    private Database from;
    private Database to;
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        if (!(to instanceof MongoDB) || (from instanceof MongoDB)) {return;}
        
        databaseDTO = from.makeDTO();
        this.from = from;
        this.to = to;
        
        createAllDocumentsAndFillData();
    }
    
    // from PostgreSQL
    private void createCollection(TableData tableData, Map<String, FieldDTO> fields, Connection connectionFrom) throws SQLException {
        
        ArrayList<Document> documents = new ArrayList<>();
        
        MongoClient mongoClient = ((MongoDB) to).getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(to.getName());
        
        db.createCollection(tableData.getTableDTO().getName());
        MongoCollection<Document> collection = db.getCollection(tableData.getTableDTO().getName());
        
        Statement statementFrom = connectionFrom.createStatement();
        String selectQuery = "SELECT " + getListOfOldFieldsNames(fields) + " FROM " + tableData.getOldName();
        ResultSet rows = statementFrom.executeQuery(selectQuery);
        
        while (rows.next()) {
            Document document = new Document();
            document.putAll(getValues(fields, rows, connectionFrom));
            documents.add(document);
        }
        collection.insertMany(documents);
    }
    
    private DBObject makeSubObject(FieldDTO field, String value, Connection connection, Map<String, FieldDTO> fields) throws SQLException {
        
        String oldRelTableName = field.getFK().getRelTableName();
        String oldRelFieldName = field.getFK().getRelFieldName();
        
        Statement statementFrom = connection.createStatement();
        String selectQuery = "SELECT * FROM " + oldRelTableName + " WHERE " + oldRelFieldName + "=" + value;
        ResultSet row = statementFrom.executeQuery(selectQuery);
        
        return new BasicDBObject(getValues(fields, row, connection));
    }
    
    private Map<String, Object> getValues(Map<String, FieldDTO> fields, ResultSet res, Connection connection) throws SQLException {
        
        List<String> fieldsValues = new ArrayList<>();
        for (String oldFieldName : fields.keySet()) {
            fieldsValues.add(res.getString(oldFieldName));
        }
        
        Map<String, Object> values = new HashMap<>();
        
        for (String key : fields.keySet()) {
            FieldDTO field = fields.get(key);
            if (field.getFK() == null) {
                values.put(field.getName(), fieldsValues.get(0));
            } else {
                values.put(field.getName(), makeSubObject(field, fieldsValues.get(0), connection, fields));
            }
            fieldsValues.remove(0);
        }
        return values;
    }
    
    private void createAllDocumentsAndFillData() {
        if (databaseDTO.getMarker() == PostgreSQL.class) {
            ToMongoDBTypeConverter.convertAllFields(databaseDTO);
            databaseDTO.getProvider().getDatabaseMetadata().forEach((tableData, fields) -> {
                try {
                    
                    ToMongoDBTypeConverter.convertAllFields(databaseDTO);
                    createCollection(tableData, fields, ((PostgreSQL) from).getConnection());
                    
                }
                catch (SQLException e) {
                    //something
                }
            });
        }
    }
}
