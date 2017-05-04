package request;
import java.io.Serializable;
import java.net.InetAddress;

import networkUtility.UDPServer;

public class CheckAliveMessage implements Serializable, Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int CHECK_ALIVE_ACK_PORT = 3332;

	public class AliveACK implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	@Override
	public void handleRequest(InetAddress addr) {
		UDPServer.sendObject(new AliveACK(), addr, CHECK_ALIVE_ACK_PORT);
	}

}
