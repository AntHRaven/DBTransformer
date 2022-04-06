package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldDTO {
    
    private String name;
    private String type;
    private boolean isPK;
    private ForeignKeyDTO FK;
  
}
