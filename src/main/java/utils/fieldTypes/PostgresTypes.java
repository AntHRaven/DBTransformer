package utils.fieldTypes;

public enum PostgresTypes implements GenericTypes {

  VARCHAR("varchar"),
  INT("int"),
  SERIAL("serial"),
  INT4("int4"),
  INT8("int8");

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
