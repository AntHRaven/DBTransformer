package dto;

import lombok.Data;

@Data
public class ForeignKeyDTO {
    private String relTableName;
    private String relFieldName;
    
    public ForeignKeyDTO(String relTableName, String relFieldName){
        this.relTableName = relTableName;
        this.relFieldName = relFieldName;
    }
}

