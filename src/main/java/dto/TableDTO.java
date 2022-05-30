package dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class TableDTO {

  @Setter
  private String name;
  private ArrayList<FieldDTO> fields;
  @Setter
  private List<Map<String, String>> data;

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof TableDTO) && Objects.equals(this.toString(), obj.toString());
  }

  @Override
  public String toString() {
    return this.name;
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}
