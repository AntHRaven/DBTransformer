package merger;

import java.sql.Connection;
import java.util.List;

public interface DBMerger<T> {
    
    Connection merge(List<T> dataBases);
}
