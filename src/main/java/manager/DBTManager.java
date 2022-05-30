package manager;

import database.Database;
import java.sql.SQLException;
import dto.DatabaseDTO;
import merger.DBMerger;
//import merger.impl.DBMergerImpl;
import merger.impl.DBMergerImpl;
import transformer.DBTransformer;
import java.util.List;

public class DBTManager {

    public void transform(Database from, Database to) throws SQLException, InterruptedException {
        DBTransformer transformer = to.getTransformer();
        transformer.transform(from, to);
    }

    public void merge(DatabaseDTO from, Database to) throws SQLException, InterruptedException {
        DBMerger merger = new DBMergerImpl();
        merger.merge(from, to);
    }

    public DatabaseDTO getMergedDTO(List<Database> from, Database to) throws SQLException {
        DBMerger merger = new DBMergerImpl();
        return merger.getMergedDto(from, to);
    }

    public DatabaseDTO getDBData(Long page, Long size, Database database) throws SQLException {
        return database.getDBData(page, size);
    }
}
