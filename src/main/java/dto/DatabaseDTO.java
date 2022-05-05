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
  // TODO: 05.05.2022 think over
  private String url;
  
  // TODO: 05.05.2022 think over
  public DatabaseDTO(Set<TableDTO> tables, String url){
    this.tables = tables;
    this.url = url;
  }
  
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
