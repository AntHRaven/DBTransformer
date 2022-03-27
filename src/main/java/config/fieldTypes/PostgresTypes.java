package config.fieldTypes;

public enum PostgresTypes {

  VARCHAR("varchar"),
  INT("int");

  private final String type;
  PostgresTypes(String type) {
    this.type = type;
  }
  
  public static PostgresTypes valueOfLabel(String label) {
    for (PostgresTypes e : values()) {
      if (e.type.equals(label)) {
        return e;
      }
    }
    return null;
  }
}
