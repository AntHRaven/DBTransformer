package models;

import utils.fieldTypes.PostgresTypes;
import lombok.Data;

@Data
public class FieldModel {

  private String name;
  private PostgresTypes type;
  private boolean isPrimary;

  public FieldModel(String name, PostgresTypes type, boolean isPrimary) {
    this.name = name;
    this.type = type;
    this.isPrimary = isPrimary;
  }
}
