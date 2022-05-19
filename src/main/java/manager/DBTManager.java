package manager;

import database.Database;
import java.sql.SQLException;
import dto.DatabaseDTO;
import merger.DBMerger;
//import merger.impl.DBMergerImpl;
import transformer.DBTransformer;
import java.util.List;

public class DBTManager {

    public void transform(Database from, Database to) throws SQLException, InterruptedException {
        DBTransformer transformer = to.getTransformer();
        transformer.transform(from, to);
    }
}
