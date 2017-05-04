package request;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * 
 * @author fredzqm
 *
 */
public abstract class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int requstID;
	protected int ackForID;

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
	 */
	public abstract void handleRequest(InetAddress address);

	/**
	 * the time interval to wait until it times out
	 * 
	 * @return
	 */
	public abstract long getTimeOut();

	/**
	 * what happens if no message acknowledged this one
	 * 
	 * @param address
	 */
	public abstract void timeOut(InetAddress address);

	public void acknowledge() {
		// hook
	}
}
