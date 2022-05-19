package data;

import dto.TableDTO;
import lombok.Getter;
import java.util.Objects;

public class TableData {

  @Getter
  String oldName;
  @Getter
  TableDTO tableDTO;

  public TableData(String oldName, TableDTO tableDTO) {
    this.oldName = oldName;
    this.tableDTO = tableDTO;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof TableData) && Objects.equals(this.oldName, ((TableData) obj).oldName);
  }

  @Override
  public int hashCode() {
    return this.oldName.hashCode();
  }
}
