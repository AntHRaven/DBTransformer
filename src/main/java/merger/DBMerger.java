package merger;


import connection.ConnectionData;
import readers.DBReader;
import transformers.DBTransformer;

import java.sql.Connection;
import java.util.List;

public interface DBMerger {

  Connection merge(List<ConnectionData<? extends DBReader, ? extends DBTransformer>> connectionDataList);
}
