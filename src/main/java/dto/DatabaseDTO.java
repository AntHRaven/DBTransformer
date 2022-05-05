package dto;

import java.util.Set;

import data.provider.Provider;
import database.Database;
import lombok.Data;

import javax.annotation.PostConstruct;

@Data
public class DatabaseDTO implements Cloneable{
  
  Set<TableDTO> tables;
  public Provider provider;
  private Class<? extends Database> marker;
  
  public DatabaseDTO(Set<TableDTO> tables, Class<? extends Database> marker){
    this.tables = tables;
    this.marker = marker;
  }
  
  @PostConstruct
  private void initializeProvider(){
    provider = new Provider(this);
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
  public Class<? extends Database> getMarker() {
    return marker;
  }
}
