package transformer.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converter.ToMongoDBTypeConverter;
import converter.types.FieldDTOMongoDBTypes;
import data.TableData;
import database.Database;
import database.MongoDB;
import database.PostgreSQL;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import org.bson.Document;
import transformer.DBTransformer;

import static transformer.FormatDataProvider.*;

public class ToMongoDBTransformer implements DBTransformer {
    
    private DatabaseDTO databaseDTO;
    private MongoClient mongoClientTo;
    private Database from;
    private Database to;
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        if (!(to instanceof MongoDB)) {return;}
        
        databaseDTO = from.makeDTO();
    
        mongoClientTo = ((MongoDB) to).getMongoClient();
        this.from = from;
        this.to = to;
        
        createAllDocumentsAndFillData();
    }
    
    // from PostgreSQL
    private void createCollection(TableData tableData, Map<String, FieldDTO> fields, Connection connectionFrom) throws SQLException {
    
        ArrayList<Document> documents = new ArrayList<>();
        
        MongoDatabase db = mongoClientTo.getDatabase(to.getName());
    
        db.createCollection(tableData.getTableDTO().getName());
        MongoCollection<Document> collection = db.getCollection(tableData.getTableDTO().getName());
    
        List<FieldDTO> PK = getPK(tableData.getTableDTO());
    
        Statement statementFrom = connectionFrom.createStatement();
        String selectQuery = "SELECT " + getListOfOldFieldsNames(fields) + " FROM " + tableData.getOldName();
        ResultSet rows = statementFrom.executeQuery(selectQuery);
    
        // TODO: 11.05.2022 if FK - do sub object
        
        while (rows.next()) {
            List<String> fieldsValues = new ArrayList<>();
            for (String oldFieldName : fields.keySet()) {
                fieldsValues.add(rows.getString(oldFieldName));
            }
            Document document = new Document();
            Map<String, Object> values = new HashMap<>();
    
            for (String key : fields.keySet()) {
                values.put(fields.get(key).getName(), getValue(fields.get(key), fieldsValues.get(0)));
                fieldsValues.remove(0);
            }
    
            document.putAll(values);
            documents.add(document);
        }
        collection.insertMany(documents);
    }
    
    // from MongoDB
    private void createCollection(TableData tableData, TableDTO tableDTO, Map<String, FieldDTO> fields, MongoClient mongoClient){
    
    }
    
    private List<FieldDTO> getPK(TableDTO tableDTO){
        List<FieldDTO> PK = new ArrayList<>();
        for (FieldDTO fieldDTO : tableDTO.getFields()) {
            if (fieldDTO.isPK()){
                PK.add(fieldDTO);
            }
        }
        return PK;
    }
    
    private Object getValue(FieldDTO field, String value){
        Class<?> marker = ((FieldDTOMongoDBTypes) field.getType()).getTypeClass();
        return marker.cast(value);
    }
    
    private void createAllDocumentsAndFillData() {
        databaseDTO.getProvider().getDatabaseMetadata().forEach((tableData, fields) -> {
            try {
                if (databaseDTO.getMarker() == MongoDB.class) {
                    MongoClient mongoClient = ((MongoDB) from).getMongoClient();
                    createCollection(tableData, tableData.getTableDTO(), fields, mongoClient);
                } else if (databaseDTO.getMarker() == PostgreSQL.class){
                    ToMongoDBTypeConverter.convertAllFields(databaseDTO);
                    createCollection(tableData, fields, ((PostgreSQL) from).getConnection());
                }
            } catch (SQLException e) {
                //something
            }
        });
    }
    
}
