package transformers.interfaces;

import databases.interfaces.DataBase;
import readers.interfaces.DocumentaryDBReader;
import readers.interfaces.KeyValueDBReader;
import readers.interfaces.RelationalDBReader;

public interface TransformerDB<DB extends DataBase> {
    public <T extends DocumentaryDBReader> void FromDocumentary(T reader);
    public <T extends KeyValueDBReader> void FromKeyValue(T reader);
    public <T extends RelationalDBReader> void FromRelational(T reader);
}
