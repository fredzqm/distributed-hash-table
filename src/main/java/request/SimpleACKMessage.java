package request;

import java.net.InetAddress;

/**
 * The most simple concrete {@link AbstractACKMessage} that triggers no action
 * when recieved
 * 
 * @author fredzqm
 *
 */
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
