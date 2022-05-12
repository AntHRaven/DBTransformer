package manager;

import database.Database;
import java.sql.SQLException;
import dto.DatabaseDTO;
import merger.DBMerger;
//import merger.impl.DBMergerImpl;
import transformer.DBTransformer;
import java.util.List;

public class DBTManager {

    //DBMerger merger = new DBMergerImpl();

//    public void merge(List<DatabaseDTO> databaseList, Database to) throws SQLException, InterruptedException {
//        merger.merge(merger.getMergedDto(databaseList), to);
//    }
//
//    public DatabaseDTO getMergedDto(List<DatabaseDTO> databaseList) throws SQLException {
//        return merger.getMergedDto(databaseList);
//    }

    public void transform(Database from, Database to) throws SQLException, InterruptedException {
        DBTransformer transformer = to.getTransformer();
        transformer.transform(from, to);
    }

    // TODO: 12.05.2022 specific transfomer for merger (how will we fill data?)
}
