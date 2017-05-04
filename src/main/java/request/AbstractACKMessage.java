package request;

import java.net.InetAddress;

public abstract class AbstractACKMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractACKMessage(int ackForID) {
		super(ackForID);
	}

	public abstract void handleRequest(InetAddress addr);

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
