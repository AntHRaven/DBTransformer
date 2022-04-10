package dto;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class DatabaseDTO {
  
  Set<TableDTO> tables;
  
  public DatabaseDTO(Set<TableDTO> tables){
    this.tables = tables;
  }

}
