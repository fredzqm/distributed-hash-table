package request;

import java.net.InetAddress;

public class SimpleACKMessage extends AbstractACKMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimpleACKMessage(int ackForID) {
		super(ackForID);
	}

	@Override
	public void handleRequest(InetAddress addr) {
		// does nothiing
	}

}
