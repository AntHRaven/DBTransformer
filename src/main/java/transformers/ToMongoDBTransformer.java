package transformers;

import readers.interfaces.DBReader;
import transformers.interfaces.DBTransformer;

public class ToMongoDBTransformer
      implements DBTransformer {
    
    @Override
    public <T extends DBReader<DocumentaryDB>> void fromDocumentary(T reader) {
    
    }
    
    @Override
    public <T extends DBReader> void fromKeyValue(T reader) {
    
    }
    
    @Override
    public <T extends RelationalDBReader> void fromRelational(T reader) {
    
    }
}
