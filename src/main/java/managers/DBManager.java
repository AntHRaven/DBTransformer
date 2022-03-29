package managers;

import connection.DataBase;
import readers.DBReader;
import transformers.DBTransformer;

import java.sql.SQLException;
import java.util.List;

public interface DBManager {

    void merge(List<DataBase<? extends DBReader, ? extends DBTransformer>> dataBaseList)
          throws SQLException;

    void transform(DataBase<? extends DBReader, ? extends DBTransformer> from,
                   DataBase<? extends DBReader, ? extends DBTransformer> to)
          throws SQLException;

}
