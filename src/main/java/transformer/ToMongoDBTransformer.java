package transformer;

import java.sql.SQLException;

import database.Database;
import dto.DatabaseDTO;
import transformer.DBTransformer;

public class ToMongoDBTransformer implements DBTransformer {
    
    private DatabaseDTO databaseDTO;
    
    @Override
    public void transform(Database from) throws SQLException {
        databaseDTO = from.makeDTO();
        
    }
}
