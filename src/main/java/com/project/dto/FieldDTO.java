package com.project.dto;

import lombok.Data;

@Data
public class FieldDTO {
    
    private String name;
    private int type;
    private boolean isPK;
    private ForeignKeyDTO FK;
    
    public FieldDTO(String name, int type, boolean isPK, ForeignKeyDTO FK){
        this.name = name;
        this.type = type;
        this.isPK = isPK;
        this.FK = FK;
    }
    
}
