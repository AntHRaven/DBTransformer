package dto;

import java.util.Map;
import lombok.Data;

@Data
public class ForeignKey {
  private String toTable;
  private String fieldsFrom;
  private String fieldsTo;
}
