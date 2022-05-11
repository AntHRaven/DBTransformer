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

import javax.print.Doc;

import static transformer.FormatDataProvider.getListOfOldFieldsNames;

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
    private void createDocument(TableData tableData, TableDTO tableDTO, Map<String, FieldDTO> fields, Connection connectionFrom) throws SQLException {
    
        Document document = new Document();
        Map<String, Object> values = new HashMap<>();
        
        MongoDatabase db = mongoClientTo.getDatabase(to.getName());
        
        db.createCollection(tableDTO.getName());
        MongoCollection<Document> collection = db.getCollection(tableDTO.getName());
        
        List<FieldDTO> PK = getPK(tableDTO);
        
        for (FieldDTO field : tableDTO.getFields()) {
            if (field.isPK()) {
                if (field.getFK() == null) {
                    values.put(field.getName(), getValue(field, PK, connectionFrom));
                } else {
                
                
                }
            } else {
                if (field.getFK() == null){
                
                
                } else {
                
                }
            }
        }
        document.putAll(values);
        collection.insertOne(document);
    }
    
    // from MongoDB
    private void createDocument(TableData tableData, TableDTO tableDTO, Map<String, FieldDTO> fields, MongoClient mongoClient){
    
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
    
    private Object getValue (FieldDTO field, List<FieldDTO> PK, Connection connectionFrom) throws SQLException {
        Class<?> marker = ((FieldDTOMongoDBTypes) field.getType()).getTypeClass();
        
        Statement statementFrom = connectionFrom.createStatement();
        String selectQuery = "SELECT " + getListOfOldFieldsNames(fields) + " FROM " + oldTableName;
        ResultSet table = statementFrom.executeQuery(selectQuery);
    }
    
    private void createAllDocumentsAndFillData() {
        databaseDTO.getProvider().getDatabaseMetadata().forEach((tableData, fields) -> {
            try {
                if (databaseDTO.getMarker() == MongoDB.class) {
                    MongoClient mongoClient = ((MongoDB) from).getMongoClient();
                    createDocument(tableData, tableData.getTableDTO(), fields, mongoClient);
                } else if (databaseDTO.getMarker() == PostgreSQL.class){
                    ToMongoDBTypeConverter.convertAllFields(databaseDTO);
                    createDocument(tableData, tableData.getTableDTO(), fields, ((PostgreSQL) from).getConnection());
                }
            } catch (SQLException e) {
                //something
            }
        });
    }
    
}
