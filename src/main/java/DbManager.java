import databases.dataBases.PostgresSQLDB;
import dto.ConnectionData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class DbManager {
    abstract Connection merge(List<ConnectionData> connectionDataList) throws SQLException;
    abstract Connection transform(ConnectionData connectionData) throws SQLException;
    
//     protected <N extends  DataBase> DBReader<N> check(ConnectionData<N> connectionData) {
//         if (connectionData instanceof ConnectionData<PostgresSQLDB>) {
//             return null;
//         }
//     }

}
