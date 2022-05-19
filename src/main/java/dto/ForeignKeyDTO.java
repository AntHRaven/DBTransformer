package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ForeignKeyDTO {

  private String relTableName;
  private String relFieldName;

}

