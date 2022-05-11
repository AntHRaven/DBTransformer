package dto;

import java.util.Set;

import data.provider.Provider;
import database.Database;
import lombok.Getter;

@Getter
public class DatabaseDTO implements Cloneable{
  
  private final Set<TableDTO> tables;
  private Provider provider;
  private final Class<? extends Database> marker;
  private final String name;
 
  public DatabaseDTO(String name, Set<TableDTO> tables, Class<? extends Database> marker){
    this.tables = tables;
    this.marker = marker;
    this.name = name;
  }
  
  public void initializeProvider(){
    provider = new Provider(this);
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
