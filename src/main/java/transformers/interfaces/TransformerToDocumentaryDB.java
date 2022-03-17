package transformers.interfaces;

import readers.interfaces.DocumentaryDBReader;
import readers.interfaces.KeyValueDBReader;
import readers.interfaces.RelationalDBReader;

public interface TransformerToDocumentaryDB{
    public void fromRelationalToDocumentary(RelationalDBReader reader);
    public void fromKeyValueToDocumentary(KeyValueDBReader reader);
    public void fromDocumentaryToDocumentary(DocumentaryDBReader reader);
}
