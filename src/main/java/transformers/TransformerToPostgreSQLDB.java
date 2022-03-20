package transformers;

import databases.PostgreSQLDB;
import readers.interfaces.DocumentaryDBReader;
import readers.interfaces.KeyValueDBReader;
import readers.interfaces.RelationalDBReader;
import transformers.interfaces.TransformerDB;

public class TransformerToPostgreSQLDB implements TransformerDB<PostgreSQLDB> {

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
