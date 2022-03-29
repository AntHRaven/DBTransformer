package dto;

import java.util.List;

import lombok.Data;

@Data
public class TableDto {
    
    private String name;
    private List<FieldDto> fields;
    private List<ForeignKey> foreignKeys;
    
}
