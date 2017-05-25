package tpc;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import protocol.ServerProtocol;
import tokenizer.FixedSeparatorMessageTokenizer;
import tokenizer.MessageTokenizer;
import tokenizer.StringMessage;
import tokenizer.StringMessageTokenizer;


/**
 * The Class ConnectionHandler.
 */
class ConnectionHandler implements Runnable {
	
	private static final int BUFFER_SIZE = 1024;
	private BufferedInputStream in;
	private PrintWriter out;
	private MessageTokenizer<StringMessage> tokenizer;
	Socket clientSocket;
	ServerProtocol<StringMessage> protocol;
	
	/**
	 * Instantiates a new connection handler.
	 *
	 * @param acceptedSocket the accepted socket
	 * @param p the ServerProtocol
	 */
	public ConnectionHandler(Socket acceptedSocket, ServerProtocol<StringMessage> p)
	{
		in = null;
		out = null;
		tokenizer = new FixedSeparatorMessageTokenizer("\n", Charset.forName("UTF-8"));
		clientSocket = acceptedSocket;
		protocol = p;
		System.out.println("Accepted connection from client!");
		System.out.println("The client is from: " + acceptedSocket.getInetAddress() + ":" + acceptedSocket.getPort());
	}
	
	/**
	 * Runs the ConnectionHandler thread.
	 */
	public void run()
	{
		
		try {
			initialize();
		}
		catch (IOException e) {
			System.out.println("Error in initializing I/O");
		}

		try {
			process();
		} 
		catch (IOException e) {
			System.out.println("Error in I/O");
		} 
		
		System.out.println("Connection closed - bye bye...");
		close();

	}
	
	/**
	 * Process new data arrival from the connected user.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void process() throws IOException
	{
		while(true){
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
		byte[] bytes = new byte[1024];
        int numBytesRead = 0;
        try {
            numBytesRead = in.read(bytes);

        } catch (IOException e) {
            numBytesRead = -1;
        }
        // is the channel closed??
        if (numBytesRead == -1) {
            // No more bytes can be read from the channel
        	close();
            // tell the protocol that the connection terminated.
            return;
        }
        if (bytes != null){
        	buf.put(bytes,0,numBytesRead);
        }
        //add the buffer to the protocol task
        buf.flip();
        tokenizer.addBytes(buf);
        //System.out.println(tokenizer.hasMessage());
        // add the protocol task to the reactor
        while (tokenizer.hasMessage()) {
            StringMessage msg = tokenizer.nextMessage();
            this.protocol.processMessage(msg, str -> {
                try {
                    ByteBuffer bytess = tokenizer.getBytesForMessage(str);
                    clientSocket.getOutputStream().write(bytess.array());
                 } catch (CharacterCodingException e) { e.printStackTrace(); }
            });

			if (protocol.isEnd(msg))
			{
				return;
			}
         }
		}
	}
	
	/**
	 * Initializinf I/O.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	// Starts listening
	public void initialize() throws IOException
	{
		in = new BufferedInputStream(clientSocket.getInputStream());
		System.out.println("I/O initialized");
	}
	
	/**
	 * Closes the connection.
	 */
	
	public void close()
	{
		try {
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}
			
			clientSocket.close();
			
		}
		catch (IOException e)
		{
			System.out.println("Exception in closing I/O");
		}
	}
	
}