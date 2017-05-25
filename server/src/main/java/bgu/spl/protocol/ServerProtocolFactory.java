package protocol;

/**
 * A factory for creating ServerProtocol objects.
 *
 * @param <T> the generic type
 */
public interface ServerProtocolFactory<T> {
	   
   	/**
   	 * Creates the ServerProtocol with the given generic type.
   	 *
   	 * @return the server protocol
   	 */
   	ServerProtocol<T> create();
}
