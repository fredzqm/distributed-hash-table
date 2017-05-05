package request;

import java.net.InetAddress;

/**
 * An abstract acknowledgement Message. ACK message does not require further
 * acknowledgement, so that it does not need to define {link
 * {@link AbstractACKMessage#getTimeOut()} and
 * {@link AbstractACKMessage#timeOut(InetAddress) and
 * {@link AbstractACKMessage#requireACK()} should return false
 * 
 * {@link AbstractACKMessage#handleRequest(InetAddress)} is still left to be
 * defined
 * 
 * @author fredzqm
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractACKMessage extends Message {

	/**
	 * constructs an ACKMessage to acknowledge ackForID
	 * 
	 * @param ackForID
	 */
	public AbstractACKMessage(int ackForID) {
		super(ackForID);
	}

	@Override
	public final long getTimeOut() {
		throw new RuntimeException("ACKMessage should not be set for timeout");
	}

	@Override
	public final void timeOut(InetAddress address) {
		throw new RuntimeException("ACKMessage should not be set for timeout");
	}

	@Override
	public final boolean requireACK() {
		return false;
	}

}
