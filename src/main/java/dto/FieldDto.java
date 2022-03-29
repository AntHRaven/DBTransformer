package dto;

import lombok.Data;
import utils.fieldTypes.PostgresTypes;

@Data
public class FieldDto {

  private String name;
  private PostgresTypes type;
  private boolean isPrimary;

  public FieldDto(String name, PostgresTypes type, boolean isPrimary) {
    this.name = name;
    this.type = type;
    this.isPrimary = isPrimary;
  }

  @Override
  public String toString() {
    return name + " " + type + (isPrimary ? " primary key" : "");
  }
}
