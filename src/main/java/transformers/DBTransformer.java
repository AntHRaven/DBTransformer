package transformers;

import java.sql.Connection;
import java.sql.SQLException;

import dto.DataBaseDto;
import readers.DBReader;

public interface DBTransformer {

    void transform(DBReader reader, Connection fromConnection, Connection toConnection) throws SQLException;
    
    void transform(DataBaseDto dataBase, Connection fromConnection, Connection toConnection) throws SQLException;
    
}
