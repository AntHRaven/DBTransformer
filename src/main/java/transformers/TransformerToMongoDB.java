package transformers;

import databases.MongoDB;
import readers.interfaces.DocumentaryDBReader;
import readers.interfaces.KeyValueDBReader;
import readers.interfaces.RelationalDBReader;
import transformers.interfaces.TransformerDB;

public class TransformerToMongoDB implements TransformerDB<MongoDB> {

    @Override
    public <T extends DocumentaryDBReader> void FromDocumentary(T reader) {

    }

    @Override
    public <T extends KeyValueDBReader> void FromKeyValue(T reader) {

    }

    @Override
    public <T extends RelationalDBReader> void FromRelational(T reader) {

    }
}
