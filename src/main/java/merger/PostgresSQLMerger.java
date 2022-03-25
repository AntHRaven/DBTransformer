package merger;

import databases.dataBases.PostgresSQLDB;

import java.sql.Connection;
import java.util.List;

public class PostgresSQLMerger implements DBMerger<PostgresSQLDB> {
    
    @Override
    public Connection merge(List<PostgresSQLDB> dataBases) {
        return null;
    }
}
