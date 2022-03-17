package transformers;

import readers.interfaces.DocumentaryDBReader;
import readers.interfaces.KeyValueDBReader;
import readers.interfaces.RelationalDBReader;
import transformers.interfaces.TransformerToRelationalDB;

public class TransformerToPostgreSQLDB implements TransformerToRelationalDB {
    @Override
    public void fromRelationalToRelational(RelationalDBReader reader) {

    }

    @Override
    public void fromKeyValueToRelational(KeyValueDBReader reader) {

    }

    @Override
    public void fromDocumentaryToRelational(DocumentaryDBReader reader) {

    }
}
