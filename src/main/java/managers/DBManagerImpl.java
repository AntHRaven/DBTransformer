package managers;

import connection.DataBase;
import java.sql.SQLException;
import readers.DBReader;
import transformers.DBTransformer;

import java.util.List;

public class DBManagerImpl
      implements DBManager {

    @Override
    public void merge(
          List<DataBase<? extends DBReader, ? extends DBTransformer>> dataBaseList) {

    }

    @Override
    public void transform(DataBase<? extends DBReader, ? extends DBTransformer> from,
                          DataBase<? extends DBReader, ? extends DBTransformer> to)
        throws SQLException {

        DBReader reader = from.getReader();
        DBTransformer transformer = to.getTransformer();
        transformer.transform(reader, from.getConnection(), to.getConnection());
    }
}
