package dto;

import java.util.ArrayList;
import java.util.Objects;

import lombok.Data;

@Data
public class TableDTO {
    
    private String name;
    private ArrayList<FieldDTO> fields;
    
    public TableDTO(String name, ArrayList<FieldDTO> fields){
        this.name = name;
        this.fields = fields;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TableDTO) && Objects.equals(this.toString(), obj.toString());
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public int hashCode(){
        return this.name.hashCode();
    }
}
