package tokenizer;

/**
 * The Class StringMessage.
 * Holds a message as a final string
 */
public class StringMessage implements Message<StringMessage> {
	
	private final String message;
	
	/**
	 * Instantiates a new string message.
	 *
	 * @param message the message
	 */
	public StringMessage(String message){
		this.message=message;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage(){
		return message;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return message;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return message.equals(other);
	}
}
