package transformers.interfaces;

import readers.interfaces.DocumentaryDBReader;
import readers.interfaces.KeyValueDBReader;
import readers.interfaces.RelationalDBReader;

public interface TransformerToKeyValueDB {
    public void fromRelationalToKeyValue(RelationalDBReader reader);
    public void fromKeyValueToKeyValue(KeyValueDBReader reader);
    public void fromDocumentaryToKeyValue(DocumentaryDBReader reader);
}
