package request;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * The messge {@link CommunictionHandler} will pass around
 * 
 * @author fredzqm
 *
 */
@SuppressWarnings("serial")
public abstract class Message implements Serializable {
	private int requstID;
	private int ackForID;

	/**
	 * this message is also ACK for another message if askForID is {@Code 0},
	 * then it means that this message does not acknowledge anyone
	 * 
	 * @param ackForID
	 */
	public Message(int ackForID) {
		this.ackForID = ackForID;
	}

	/**
	 * set the requestiD for this message, generated with random number
	 * generator
	 * 
	 * @param requestID
	 */
	public void setRequestID(int requestID) {
		this.requstID = requestID;
	}

	/**
	 * 
	 * @return the ID of this request
	 */
	public int getRequestID() {
		return requstID;
	}

	/**
	 * 
	 * @return the requestID for the message this message acknowledging for
	 */
	public int getACKID() {
		return ackForID;
	}

	/**
	 * Unless this is a pure ACK message, by default it requires a response
	 * 
	 * @return
	 */
	public boolean requireACK() {
		return true;
	}

	/**
	 * 
	 * @param address
	 *            the address this message is from
	 * @param acknowleged 
	 */
	public abstract void handleRequest(InetAddress address, Message acknowleged);

	/**
	 * the time interval to wait until it times out
	 * 
	 * @return
	 */
	public abstract long getTimeOut();

	/**
	 * When no message acknowledged this one, after
	 * {@link Message#getTimeOut()}, this method will be called
	 * 
	 * @param address
	 *            the address this message were supposed to be sent to
	 */
	public abstract void timeOut(InetAddress address);
}
