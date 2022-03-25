package transformers;

import readers.MongoDBReader;
import readers.PostgreSQLDBReader;
import readers.interfaces.DBReader;

import java.sql.Connection;

public interface DBTransformer {
//    public <T extends DBReader<DocumentaryDB>> void fromDocumentary(T reader);
//    public <T extends DBReader> void fromKeyValue(T reader);
//    public <T extends RelationalDBReader> void fromRelational(T reader);

    public void transform(DBReader reader);

}
