package dto;

import dto.relational.dataBases.MySqlDB;

public abstract class DataBase {

  private String dbName;
  private String connectionURL;

  public abstract void transformToDto(String connectionURL);
  public void transformToDB(String connectionURL, String type) {
//    if(type == "mySql") {
//      return new MySqlDB().transformToDB();
//    }
  }
}
