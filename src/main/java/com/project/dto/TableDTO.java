package com.project.dto;

import java.util.ArrayList;
import lombok.Data;

@Data
public class TableDTO {
    
    private String name;
    private ArrayList<FieldDTO> fields;
    
    public TableDTO(String name, ArrayList<FieldDTO> fields){
        this.name = name;
        this.fields = fields;
    }
    
}
