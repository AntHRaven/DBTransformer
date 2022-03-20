package readers.interfaces;

import java.util.ArrayList;
import java.util.Map;

public interface RelationalDBReader extends DBReader{
    // methods for relational data bases

    //for example
    ArrayList<String> getAllTablesNames();
    Map<String, String> getAllFieldsNames(String tableName);
    ArrayList<String> getPrimaryKeyColumnsNames(String tableName);
    ArrayList<String> getForeignKeyColumnsNames(String tableName);
}
