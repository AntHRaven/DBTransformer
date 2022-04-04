package dto;

import java.util.List;
import lombok.Data;

@Data
public class DatabaseDTO {
  
  List<TableDTO> tables;
  
  public DatabaseDTO(List<TableDTO> tables){
    this.tables = tables;
  }

}
