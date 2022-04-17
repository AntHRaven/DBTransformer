package dto;

import java.util.Set;
import lombok.Data;

@Data
public class DatabaseDTO implements Cloneable{
  
  Set<TableDTO> tables;
  
  public DatabaseDTO(Set<TableDTO> tables){
    this.tables = tables;
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
