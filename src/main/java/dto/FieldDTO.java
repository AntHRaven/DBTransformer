package dto;

import converter.types.FieldDTOTypes;
import lombok.Data;

@Data
public class FieldDTO {
    
    private String name;
    private FieldDTOTypes type;
    private boolean isPK;
    private ForeignKeyDTO FK;
    
    public FieldDTO(String name, FieldDTOTypes type, boolean isPK, ForeignKeyDTO FK){
        this.name = name;
        this.type = type;
        this.isPK = isPK;
        this.FK = FK;
    }
    
}
