package transformer;

import java.sql.SQLException;

import database.Database;
import dto.DatabaseDTO;
import transformer.DBTransformer;

public class ToPostgresDBTransformer implements DBTransformer {
    
    private DatabaseDTO databaseDTO;
    
    @Override
    public void transform(Database from, Database to) throws SQLException {
        //надо тогда какую то проверку делать что пришла нужная база
        databaseDTO = from.makeDTO();
        
    }
    
}
