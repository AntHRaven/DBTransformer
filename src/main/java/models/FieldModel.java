package models;

import config.fieldTypes.PostgresTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
