package transformers.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import readers.DBReader;
import transformers.DBTransformer;

public class ToMongoDBTransformer implements DBTransformer {


  @Override
  public void transform(DBReader reader, Connection fromConnection, Connection toConnection)
      throws SQLException {

  }
}
