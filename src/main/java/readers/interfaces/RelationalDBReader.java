package readers.interfaces;

import java.util.ArrayList;

public interface RelationalDBReader extends DBReader{
    // methods for relational data bases

    //for example
    ArrayList<String> getAllTablesNames();
    ArrayList<String> getAllFieldsNames(String tableName);
    ArrayList<String> getPrimaryKeyColumnsNames(String tableName);
    ArrayList<String> getForeignKeyColumnsNames(String tableName);
}
