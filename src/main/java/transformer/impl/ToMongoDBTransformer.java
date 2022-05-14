package transformer.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
    
    private DBObject makeSubObject(FieldDTO field, Object value, Connection connection) throws SQLException {
        
        String oldRelTableName = field.getFK().getRelTableName();
        String oldRelFieldName = field.getFK().getRelFieldName();
        
        Statement statementFrom = connection.createStatement();
        String selectQuery = "SELECT * FROM " + oldRelTableName + " WHERE " + oldRelFieldName + "=" + value;
        ResultSet row = statementFrom.executeQuery(selectQuery);
        
        Map<String, FieldDTO> fields = new HashMap<>();
        
        for (TableData tableData : databaseDTO.getProvider().getDatabaseMetadata().keySet()) {
            if (tableData.getOldName().equals(oldRelTableName)){
                fields = databaseDTO.getProvider().getDatabaseMetadata().get(tableData);
            }
        }
        if (row.next()) {
            return new BasicDBObject(getValues(fields, row, connection));
        } else {
            return null;
        }
    }
    private Map<String, Object> getValues(Map<String, FieldDTO> fields, ResultSet res, Connection connection) throws SQLException {
        
        Map<String, Object> values = new HashMap<>();
        for (String oldFieldName : fields.keySet()) {
            FieldDTO field = fields.get(oldFieldName);
            if (field.getFK() == null) {
                System.out.println(res.getObject(oldFieldName));
                System.out.println(fields.get(oldFieldName).getName());
                System.out.println("-----------------------");
                values.put(fields.get(oldFieldName).getName(), res.getObject(oldFieldName));
            } else {
                values.put(fields.get(oldFieldName).getName(), makeSubObject(field, res.getObject(oldFieldName), connection));
            }
        }
        return values;
    }
    
    
    
    private void createAllDocumentsAndFillData() {
        if (databaseDTO.getMarker() == PostgreSQL.class) {
            ToMongoDBTypeConverter.convertAllFields(databaseDTO);
            databaseDTO.getProvider().getDatabaseMetadata().forEach((tableData, fields) -> {
                try {
                    
                    ToMongoDBTypeConverter.convertAllFields(databaseDTO);
                    if(Objects.equals(tableData.getTableDTO().getName(), "message")) {
                        System.out.println(tableData.getOldName());
                    }
                    createCollection(tableData, fields, ((PostgreSQL) from).getConnection());
                    
                }
                catch (SQLException e) {
                    //something
                }
            });
        }
    }
}
