import java.io.Serializable;
import java.net.InetAddress;

public class CheckAliveMessage implements Serializable, Request {

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
