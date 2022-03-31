package merger;


import connection.DataBase;
import dto.DataBaseDto;
import readers.DBReader;
import transformers.DBTransformer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public interface DBMerger {
    
    Connection mergeFromReader(List<DataBase<? extends DBReader, ? extends DBTransformer>> dataBaseList);
    
    Connection mergeFromDto(List<DataBaseDto> dataBaseList);
    
    
    
}
