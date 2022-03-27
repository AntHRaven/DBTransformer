package connection;

import readers.DBReader;
import transformers.DBTransformer;
import java.sql.Connection;

public abstract class ConnectionData<R extends DBReader, T extends DBTransformer> {
  
  public String url;
  public String user;
  public String password;
  
  public ConnectionData(String url) {
    this.url = url;
  }
  
  public ConnectionData(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
  }
  
  abstract public Connection getConnection();

  abstract public R getReader();

  abstract public T getTransformer();

}
