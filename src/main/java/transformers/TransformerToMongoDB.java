package transformers;

import readers.interfaces.DocumentaryDBReader;
import readers.interfaces.KeyValueDBReader;
import readers.interfaces.RelationalDBReader;
import transformers.interfaces.TransformerToDocumentaryDB;

public class TransformerToMongoDB implements TransformerToDocumentaryDB {

  @Override
  public void fromRelationalToDocumentary(RelationalDBReader reader) {
    // when we, for example as in main class, have postgre -> mongo
    // here we use reader methods
    // if it's postgre -- PostgresDBReader

    System.out.println(reader.getAllTablesNames());
    for (String tableName : reader.getAllTablesNames()) {
      System.out.println(reader.getAllFieldsNames(tableName));
    }
//        reader.getAllFieldsNames("TableName");

    // using these methods we can create new mongo db from any relational
    // target of this method = make mongo, using reader's methods (getting data)

    // we need to think about returning value (maybe connection to new mongo db)
  }

  @Override
  public void fromKeyValueToDocumentary(KeyValueDBReader reader) {

  }

  @Override
  public void fromDocumentaryToDocumentary(DocumentaryDBReader reader) {

  }
}
