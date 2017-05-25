package tokenizer;

/**
 * A factory for creating Tokenizer objects.
 *
 * @param <T> the generic type
 */
public interface TokenizerFactory<T> {
   
   /**
    * Creates the MessageTokenizer.
    *
    * @return the MessageTokenizer created
    */
   MessageTokenizer<T> create();
}
