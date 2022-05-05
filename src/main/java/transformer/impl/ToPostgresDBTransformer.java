package transformer.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.mongodb.MongoClient;
import converter.ToPostgreSQLTypeConverter;
import database.Database;
import database.MongoDB;
import database.PostgreSQL;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import transformer.DBTransformer;

public class ToPostgresDBTransformer implements DBTransformer {
    
    private DatabaseDTO databaseDTO;
    private Connection connectionTo;
    private Database from;
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        if (!(to instanceof PostgreSQL)) return;
        
        connectionTo = ((PostgreSQL) to).getConnection();
        databaseDTO = from.makeDTO();
        this.from = from;
        createTables();
    }
    
    @Override
    public void fillAllData(){
        //can do more threads here (for current table)
        databaseDTO.provider.databaseMetadata.forEach((tableData, fields) -> {
            try {
                if (databaseDTO.getMarker() == MongoDB.class) {
                    MongoClient mongoClient = ((MongoDB) from).getMongoClient();
                    ToPostgreSQLTypeConverter.convertAllFields(databaseDTO);
                    fillCollectionsTable();
                    for (String collectionName : from.getNames()) {
                        // for each collection gwt data from documents
                        // we need to keep data about collection name for current table !!!
                        
                        }
                        fillTableData(tableData.getOldName(), tableData.getTableDTO().getName(), fields, mongoClient);
                } else if (databaseDTO.getMarker() == PostgreSQL.class){
                    fillTableData(tableData.getOldName(), tableData.getTableDTO().getName(), fields, ((PostgreSQL) from).getConnection());
                }
            } catch (SQLException e) {
                //nothing?
            }
        });
    }
    
    // from PostgreSQL method
    private void fillTableData(String oldTableName, String newTableName, Map<String, FieldDTO> fields, Connection connectionFrom) throws SQLException {
        Statement statementFrom = connectionFrom.createStatement();
        String selectQuery = "SELECT " + getListOfOldFieldsNames(fields) + " FROM " + oldTableName;
        ResultSet table = statementFrom.executeQuery(selectQuery);
        
        while (table.next()){
            ArrayList<String> values = new ArrayList<>();
            for (String oldFieldName : fields.keySet()) {
                values.add(table.getString(oldFieldName));
            }
            String insertOneRowQuery =
                  "INSERT INTO " + newTableName + "(" + getListOfNewFieldsNames(fields) + ") VALUES (" + getListOfValues(values) + ")";
            Statement statementTo = connectionTo.createStatement();
            statementTo.executeQuery(insertOneRowQuery);
        }
    }
    
    // from MongoDB method
    private void fillTableData(String oldTableName, String newTableName, Map<String, FieldDTO> fields, MongoClient mongoClientFrom) throws SQLException {
        // each doc has field "collection_name" -- wee need to fill it
        String collectionFieldName = "collection_name";
        
        // if doc name contains two or more "_"
        if (true){
            // that means - this table is from SUB document
            
            // split by "_"
            // second word = document _id
            
            // we have to check if each of fields are objects or not
            
            // get document from collection by name (old name ! )
            
            // then - foreach field dto in table
            // - get from document by old field name the value
            
            // before getting the value we should check if field is FK o not
            // if not - everything is ok, so we can get the value
            // if FK - we need to add _id to relTable
            // and add a row to that relTable with the same _id
            
            // do it recursively
            
        } else {
            // that means - table is like "root" document
            
            // split by "_", first word = document, second = value of _id
            // get all fields by old names and fill in tables with new names
        }
    }
    
    private void fillCollectionsTable(){
        // положить имена все коллекций в таблицу с новым именем, по старому (указано ниже)
        String collectionTableOldName = "collections";
        from.getNames();
    }
    
    private String getListOfOldFieldsNames(Map<String, FieldDTO> fields){
        StringBuilder list = new StringBuilder();
        for (String key : fields.keySet()) {
            list.append(key).append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    private String getListOfNewFieldsNames(Map<String, FieldDTO> fields){
        StringBuilder list = new StringBuilder();
        for (String key : fields.keySet()) {
            list.append(fields.get(key).getName()).append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    private String getListOfValues(ArrayList<String> values){
        StringBuilder list = new StringBuilder();
        for (String val : values) {
            list.append("'").append(val).append("'").append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
    
    // creating tables
    
    private void createTables() throws SQLException {
        StringBuilder createAllTablesSQL = new StringBuilder();
        StringBuilder addAllForeignKeysSQL = new StringBuilder();
        
        for (TableDTO table : databaseDTO.getTables()) {
            createAllTablesSQL.append(generateSQLCreateTable(table));
            addAllForeignKeysSQL.append(generateSQLForeignKeys(table));
        }
        Statement statement = connectionTo.createStatement();
        statement.executeUpdate(createAllTablesSQL.toString());
        statement.executeUpdate(addAllForeignKeysSQL.toString());
        statement.close();
    }
    
    private String generateSQLFields(List<FieldDTO> fields) {
        StringBuilder fieldsString = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            fieldsString
                  .append(fields.get(i).getName())
                  .append(" ")
                  .append(fields.get(i).getType())
                  .append(fields.get(i).isPK() ? " primary key" : "");
            if (i != fields.size() - 1) {
                fieldsString.append(", ");
            }
        }
        return fieldsString.toString();
    }
    
    private String generateSQLForeignKeys(TableDTO table) {
        StringBuilder addForeignKeysSQL = new StringBuilder();
        for (FieldDTO fields : table.getFields()) {
            if (fields.getFK() != null) {
                String fkName = "fk_" + table.getName() + "_" + fields.getFK().getRelTableName();
                addForeignKeysSQL
                      .append("alter table ")
                      .append(table.getName())
                      .append(" drop constraint if exists ")
                      .append(fkName)
                      .append(";")
                      .append(" alter table ")
                      .append(table.getName())
                      .append(" add constraint ")
                      .append(fkName)
                      .append(" foreign key (")
                      .append(fields.getName())
                      .append(") ")
                      .append("references ")
                      .append(fields.getFK().getRelTableName())
                      .append(" (")
                      .append(fields.getFK().getRelFieldName())
                      .append("); ");
            }
        }
        return addForeignKeysSQL.toString();
    }
    
    private String generateSQLCreateTable(TableDTO table) {
        return "create table if not exists " +
               table.getName() +
               " ( " +
               generateSQLFields(table.getFields()) +
               "); ";
    }
    
}
