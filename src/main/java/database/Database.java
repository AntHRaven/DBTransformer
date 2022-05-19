package database;

import dto.DatabaseDTO;
import lombok.Getter;
import transformer.DBTransformer;
import java.sql.SQLException;
import java.util.List;

public abstract class Database {

  @Getter
  protected List<String> names;
  @Getter
  protected String name;
  protected DBTransformer dbTransformer;

  abstract public <T extends DBTransformer> T getTransformer();

  abstract public DatabaseDTO makeDTO() throws SQLException;

  Database(String dbName, List<String> names) {
    this.name = dbName;
    this.names = names;
  }
}
