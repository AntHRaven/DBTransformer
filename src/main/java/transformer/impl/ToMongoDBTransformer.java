package transformer.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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

import java.util.concurrent.ThreadPoolExecutor;

import org.bson.Document;
import transformer.DBTransformer;

import static data.provider.FormatDataProvider.*;

public class ToMongoDBTransformer
      implements DBTransformer {
    
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private DatabaseDTO databaseDTO;
    private Database from;
    private Database to;
    
    @Override
    public void transform(Database from, Database to) throws SQLException, InterruptedException {
        if (!(to instanceof MongoDB) || (from instanceof MongoDB)) {
            return;
        }
        
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
    
    private Object getValue(FieldDTO field, String value) {
        Class<?> marker = ((FieldDTOMongoDBTypes) field.getType()).getTypeClass();
        return marker.cast(value);
    }
    
    private void createAllDocumentsAndFillData() throws InterruptedException {
        LinkedBlockingQueue<Callable<String>> callablesCreateDocumentsTasks = new LinkedBlockingQueue<>();
        
        databaseDTO.getProvider().getDatabaseMetadata().forEach((tableData, fields) -> {
            try {
                if (databaseDTO.getMarker() == PostgreSQL.class) {
                    ToMongoDBTypeConverter.convertAllFields(databaseDTO);
                    callablesCreateDocumentsTasks.add(new ToMongoDBTransformer.CreateAllDocumentsAndFillDataTask(tableData, fields,
                                                                                                                 ((PostgreSQL) from).getConnection()));
                }
            }
            catch (SQLException e) {
                //something
            }
        });
        executor.invokeAll(callablesCreateDocumentsTasks);
        callablesCreateDocumentsTasks.clear();
    }
    
    public static class CreateAllDocumentsAndFillDataTask
          implements Callable<String> {
        
        TableData tableData;
        Map<String, FieldDTO> fields;
        Connection connectionFrom;
        
        public CreateAllDocumentsAndFillDataTask(TableData tableData, Map<String, FieldDTO> fields, Connection connectionFrom) {
            this.tableData = tableData;
            this.fields = fields;
            this.connectionFrom = connectionFrom;
        }
        
        @Override
        public String call() throws SQLException {
            ToMongoDBTransformer transformer = new ToMongoDBTransformer();
            transformer.createCollection(tableData, fields, connectionFrom);
//            connection.createStatement().executeQuery(transformer.generateSQLForeignKeys(tableDTO));
            return null;
        }
    }
    
}
