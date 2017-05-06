package request;

import java.net.InetAddress;

/**
 * The most simple concrete {@link AbstractACKMessage} that triggers no action
 * when recieved
 * 
 * @author fredzqm
 *
 */
public class ACK extends AbstractACKMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ACK(int ackForID) {
		super(ackForID);
	}

	@Override
	public void handleRequest(InetAddress addr, Message acknowleged) {
		// does nothiing
	}

}
