package merger;


import connection.DataBase;
import readers.DBReader;
import transformers.DBTransformer;

import java.sql.Connection;
import java.util.List;

public interface DBMerger {
    
    Connection merge(List<DataBase<? extends DBReader, ? extends DBTransformer>> dataBaseList);
}
