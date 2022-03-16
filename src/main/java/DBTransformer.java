import dto.DataBase;
import dto.relational.dataBases.MySqlDB;

public class DBTransformer {

  public static void main(String[] args) {
    DataBase db = new MySqlDB();
    db.transformToDto("tasdafa");
    db.transformToDB();
  }
}
