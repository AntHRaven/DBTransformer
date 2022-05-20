package data;

import dto.FieldDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

@Getter
@Setter
public class SubObjectData {

  private Map<String, String> values;
  private Map<NameType, List<String>> mapName;
  private Document field;
  private String newTableName;
  private Map<String, FieldDTO> fields;

  public SubObjectData(Map<String, String> values, Map<NameType, List<String>> mapName, Document field, String newTableName, Map<String, FieldDTO> fields){
    this.values = new HashMap<>(values);
    this.mapName = mapName;
    this.field = field;
    this.fields = fields;
    this.newTableName = newTableName;
  }

}
