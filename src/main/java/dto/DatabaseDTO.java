package dto;

import java.util.Set;

import data.provider.Provider;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.PostConstruct;

@Data
@NoArgsConstructor
public class DatabaseDTO implements Cloneable{
  
  Set<TableDTO> tables;
  public Provider provider;
  private String url;
  
  public DatabaseDTO(Set<TableDTO> tables, String url){
    this.tables = tables;
    this.url = url;
  }
  
  public DatabaseDTO(Set<TableDTO> tables){
    this.tables = tables;
  }
  
  @PostConstruct
  private void initializeProvider(){
    provider = new Provider(this);
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
