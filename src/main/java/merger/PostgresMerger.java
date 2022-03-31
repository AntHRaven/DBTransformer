package merger;

import connection.DataBase;
import dto.DataBaseDto;
import readers.DBReader;
import transformers.DBTransformer;

import java.sql.Connection;
import java.util.List;

public class PostgresMerger
      implements DBMerger {
    
    @Override
    public Connection mergeFromReader(List<DataBase<? extends DBReader, ? extends DBTransformer>> dataBaseList) {
        return null;
    }
    
    @Override
    public Connection mergeFromDto(List<DataBaseDto> dataBaseList) {
        return null;
    }
}
