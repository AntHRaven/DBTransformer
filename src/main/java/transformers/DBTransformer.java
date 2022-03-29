package transformers;

import java.sql.Connection;
import java.sql.SQLException;
import readers.DBReader;

public interface DBTransformer {
//    public <T extends DBReader<DocumentaryDB>> void fromDocumentary(T reader);
//    public <T extends DBReader> void fromKeyValue(T reader);
//    public <T extends RelationalDBReader> void fromRelational(T reader);

  public void transform(DBReader reader, Connection fromConnection, Connection toConnection) throws SQLException;

}
