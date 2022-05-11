package dto;

import converter.types.FieldDTOTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class FieldDTO {
    @Setter
    private String name;
    private FieldDTOTypes type;
    private boolean isPK;
    private ForeignKeyDTO FK;
}
