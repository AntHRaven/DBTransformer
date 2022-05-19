package converter;

import converter.types.FieldDTOMongoDBTypes;
import converter.types.FieldDTOPostgreSQLTypes;
import dto.DatabaseDTO;
import dto.FieldDTO;
import dto.TableDTO;
import java.sql.Types;

public class ToPostgreSQLTypeConverter {

  public static void convertAllFields(DatabaseDTO databaseDTO) {
    for (TableDTO table : databaseDTO.getTables()) {
      for (FieldDTO field : table.getFields()) {
        if (field.getType() instanceof FieldDTOPostgreSQLTypes) {
          continue;
        }
        field.setType(convert((FieldDTOMongoDBTypes) field.getType()));
      }
    }
  }

  private static FieldDTOPostgreSQLTypes convert(FieldDTOMongoDBTypes type) {
    switch (type) {
      case STRING, OBJECT_ID: {
        return FieldDTOPostgreSQLTypes.TEXT;
      }
      case BINARY_DATA: {
        return FieldDTOPostgreSQLTypes.BYTEA;
      }
      case BOOLEAN: {
        return FieldDTOPostgreSQLTypes.BOOLEAN;
      }
      case DATE: {
        return FieldDTOPostgreSQLTypes.DATE;
      }
      case DOUBLE: {
        return FieldDTOPostgreSQLTypes.DOUBLE_PRECISION;
      }
      case INTEGER: {
        return FieldDTOPostgreSQLTypes.INTEGER;
      }
      case LONG: {
        return FieldDTOPostgreSQLTypes.BIGINT;
      }
      case NULL: {
        return FieldDTOPostgreSQLTypes.NULL;
      }
      case DECIMAL_128: {
        return FieldDTOPostgreSQLTypes.DECIMAL;
      }
      case TIMESTAMP: {
        return FieldDTOPostgreSQLTypes.TIMESTAMP;
      }
      default:
        return FieldDTOPostgreSQLTypes.TEXT;
    }
  }

  public static FieldDTOPostgreSQLTypes getTypeWithName(int type) {
    switch (type) {
      case Types.SMALLINT: {
        return FieldDTOPostgreSQLTypes.SMALLINT;
      }
      case Types.INTEGER: {
        return FieldDTOPostgreSQLTypes.INTEGER;
      }
      case Types.BIGINT: {
        return FieldDTOPostgreSQLTypes.BIGINT;
      }
      case Types.REAL: {
        return FieldDTOPostgreSQLTypes.REAL;
      }
      case Types.DOUBLE: {
        return FieldDTOPostgreSQLTypes.DOUBLE_PRECISION;
      }
      case Types.NUMERIC: {
        return FieldDTOPostgreSQLTypes.NUMERIC;
      }
      case Types.DECIMAL: {
        return FieldDTOPostgreSQLTypes.DECIMAL;
      }
      case Types.CHAR: {
        return FieldDTOPostgreSQLTypes.CHAR;
      }
      case Types.VARCHAR: {
        return FieldDTOPostgreSQLTypes.VARCHAR;
      }
      case Types.DATE: {
        return FieldDTOPostgreSQLTypes.DATE;
      }
      case Types.TIME: {
        return FieldDTOPostgreSQLTypes.TIME;
      }
      case Types.TIMESTAMP: {
        return FieldDTOPostgreSQLTypes.TIMESTAMP;
      }
      case Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY: {
        return FieldDTOPostgreSQLTypes.BYTEA;
      }
      case Types.NULL: {
        return FieldDTOPostgreSQLTypes.NULL;
      }
      case Types.BOOLEAN: {
        return FieldDTOPostgreSQLTypes.BOOLEAN;
      }
      case Types.TIME_WITH_TIMEZONE: {
        return FieldDTOPostgreSQLTypes.TIME_WITH_TIMEZONE;
      }
      case Types.TIMESTAMP_WITH_TIMEZONE: {
        return FieldDTOPostgreSQLTypes.TIMESTAMP_WITH_TIME_ZONE;
      }
      default: {
        return FieldDTOPostgreSQLTypes.TEXT;
      }
    }
  }

  private ToPostgreSQLTypeConverter() {
  }


}
