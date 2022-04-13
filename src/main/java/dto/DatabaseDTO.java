package dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DatabaseDTO implements Cloneable {

  
  Set<TableDTO> tables;
  String url;
  
  public DatabaseDTO(Set<TableDTO> tables){
    this.tables = tables;
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
