package merger;


import connection.DataBase;
import dto.DataBaseDto;
import readers.DBReader;
import transformers.DBTransformer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public interface DBMerger {
    //TODO ошибка при перегрузке метода с листом DataBaseDto
    
    Connection mergeFromReader(List<DataBase<? extends DBReader, ? extends DBTransformer>> dataBaseList);
    
    Connection mergeFromDto(List<DataBaseDto> dataBaseList);
    
    
    
}
