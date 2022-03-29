package managers;

import connection.DataBase;

import java.sql.SQLException;

import dto.DataBaseDto;
import readers.DBReader;
import transformers.DBTransformer;

import java.util.ArrayList;
import java.util.List;

public class DBManagerImpl
      implements DBManager {
    
    
    @Override
    public void merge(List<DataBase<? extends DBReader, ? extends DBTransformer>> dataBaseList,
                      DataBase<? extends DBReader, ? extends DBTransformer> to) throws SQLException {
        
    }
    
    @Override
    public void transform(DataBase<? extends DBReader, ? extends DBTransformer> from,
                          DataBase<? extends DBReader, ? extends DBTransformer> to)
          throws SQLException {
        
        DBReader reader = from.getReader();
        DBTransformer transformer = to.getTransformer();
        transformer.transform(reader, from.getConnection(), to.getConnection());
    }
    
    @Override
    public DataBaseDto getDataBaseInfo(DataBase<? extends DBReader, ? extends DBTransformer> dataBase) {
        return dataBase.getReader().getDataBaseInfo(dataBase.getConnection());
    }
    
    @Override
    public List<DataBaseDto> getAllDataBaseInfo(List<DataBase<? extends DBReader, ? extends DBTransformer>> dataBases) {
        List<DataBaseDto> dataBaseDtoList = new ArrayList<>();
        for (DataBase<? extends DBReader, ? extends DBTransformer> dataBase : dataBases) {
            dataBaseDtoList.add(dataBase.getReader().getDataBaseInfo(dataBase.getConnection()));
        }
        return dataBaseDtoList;
    }
    
}
