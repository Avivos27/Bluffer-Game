package protocol;

/**
 * A factory for creating AsyncServerProtocol objects.
 *
 * @param <T> the generic type
 */
public interface AsyncServerProtocolFactory<T> extends ServerProtocolFactory<T> {
	   
   	/* 
   	 * Creates a single AsyncServerProtocol
   	 */
   	AsyncServerProtocol<T> create();
}

