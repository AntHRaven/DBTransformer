package converter.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum FieldDTOPostgreSQLTypes implements FieldDTOTypes{
    
    CHAR("CHAR"),
    VARCHAR("VARCHAR"),
    TEXT("TEXT"),
    BYTEA("BYTEA"),
    DATE("DATE"),
    TIME("TIME"),
    TIMESTAMP("TIMESTAMP"),
    TIME_WITH_TIMEZONE("TIME_WITH_TIMEZONE"),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE"),
    INTERVAL("INTERVAL"),
    SERIAL("SERIAL"),
    SMALLSERIAL("SMALLSERIAL"),
    BIGSERIAL("BIGSERIAL"),
    SMALLINT("SMALLINT"),
    INTEGER("INTEGER"),
    BIGINT("BIGINT"),
    NUMERIC("NUMERIC"),
    DECIMAL("DECIMAL"),
    FLOAT("FLOAT"),
    REAL("REAL"),
    DOUBLE_PRECISION("DOUBLE PRECISION"),
    BOOLEAN("BOOLEAN"),
    NULL("NULL");
    
    private String type;
    
    public String getType() {
        return type;
    }
}
