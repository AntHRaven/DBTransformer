package transformers.interfaces;

import readers.interfaces.DocumentaryDBReader;
import readers.interfaces.KeyValueDBReader;
import readers.interfaces.RelationalDBReader;

public interface TransformerToRelationalDB{
    public void fromRelationalToRelational(RelationalDBReader reader);
    public void fromKeyValueToRelational(KeyValueDBReader reader);
    public void fromDocumentaryToRelational(DocumentaryDBReader reader);
}
