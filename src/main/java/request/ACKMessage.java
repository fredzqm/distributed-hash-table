package request;

import java.net.InetAddress;

public class ACKMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ACKMessage(int ackForID) {
		super(ackForID);
	}

	@Override
	public void handleRequest(InetAddress addr) {
		// since it is just for ACK, do nothing here;
	}

	@Override
	public long getTimeOut() {
		return 0; // no need for timeout
	}

	@Override
	public void timeOut(InetAddress address) {
		throw new RuntimeException("ACKMessage should not be set for timeout");
	}

	@Override
	public boolean requireACK() {
		return false;
	}

}
