package transformer;

import java.sql.SQLException;
import database.Database;
import dto.DatabaseDTO;

public interface DBTransformer {
    void transform(Database from, Database to) throws SQLException, InterruptedException;
    //при мерже - заполнение делаем по-другому!
}

