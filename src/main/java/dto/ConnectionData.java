package dto;

import readers.interfaces.DBReader;
import transformers.interfaces.DBTransformer;

import java.sql.Connection;

public interface ConnectionData<R extends DBReader, T extends DBTransformer> {
   
    Connection getConnection();
     R getReader();
     T getTransformer();
}
