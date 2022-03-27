package utils.fieldTypes;

public enum PostgresTypes implements GenericTypes {

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
