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
@AllArgsConstructor
public class SubObjectData {

  private Map<String, String> values;
  private Map<NameType, List<String>> mapName;
  private Document field;
  private String newTableName;
  private Map<String, FieldDTO> fields;

}
