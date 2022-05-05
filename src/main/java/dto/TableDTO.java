package dto;

import java.util.ArrayList;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {
    
    private String name;
    private ArrayList<FieldDTO> fields;
    
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
