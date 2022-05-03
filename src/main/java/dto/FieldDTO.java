package dto;

import converter.types.FieldDTOTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDTO {
    
    private String name;
    private String type;
    private boolean isPK;
    private ForeignKeyDTO FK;
    
}
