package protocol;

import mics.SharedDS;

/**
 * A factory for creating TBGProtocol objects.
 */
public class TBGProtocolFactory implements ServerProtocolFactory {
	private SharedDS ds = SharedDS.getInstance();
	
	/* (non-Javadoc)
	 * @see bgu.spl.protocol.ServerProtocolFactory#create()
	 */
	@Override
	public ServerProtocol create() {
		return new TBGProtocol(ds);
	}

}
