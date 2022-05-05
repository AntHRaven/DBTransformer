package data;

import dto.TableDTO;
import java.util.Objects;

public class TableData {
    String oldName;
    TableDTO tableDTO;
    
    public TableData(String oldName, TableDTO tableDTO){
        this.oldName = oldName;
        this.tableDTO = tableDTO;
    }
    
    public String getOldName() {
        return oldName;
    }
    
    public TableDTO getTableDTO() {
        return tableDTO;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TableData) && Objects.equals(this.oldName, ((TableData) obj).oldName);
    }
    
    @Override
    public int hashCode(){
        return this.oldName.hashCode();
    }
}
