package dto;

import java.util.Set;

import data.provider.Provider;
import lombok.Data;

import javax.annotation.PostConstruct;

@Data
public class DatabaseDTO implements Cloneable{
  
  Set<TableDTO> tables;
  public Provider provider;
  
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
