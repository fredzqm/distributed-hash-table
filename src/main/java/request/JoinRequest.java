package request;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import distributedHashTable.DistributedHashTable;
import distributedHashTable.RequestParser;
import networkUtility.UDPServer;

public class JoinRequest implements Request {
	public static int JOIN_RESPONSE_PORT = 3330;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void sendJoinRequest(InetAddress entryNode) {
		JoinRequest joinRequest = new JoinRequest();
		UDPServer.sendObject(joinRequest, entryNode, RequestParser.PORT);
		try {
			JoinResponse response = UDPServer.recieveObject(entryNode, JOIN_RESPONSE_PORT, 10000, JoinResponse.class);
			System.out.println("getResponse: " + response.yourRightIP);
			DistributedHashTable dht = DistributedHashTable.getIntance();
			if (response.yourRightIP != null)
				dht.setRight(InetAddress.getByName(response.yourRightIP));
			else
				dht.setRight(entryNode);
			dht.setLeft(entryNode);
			dht.checkNeighbor();
			UDPServer.sendObject(new JoinACK(), entryNode, RequestParser.PORT);
		} catch (SocketTimeoutException e) {
			throw new RuntimeException("join cluster request timed out");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handleRequest(InetAddress addr) {
		System.out.println("Hello world");
		UDPServer.sendObject(new JoinResponse(), addr, JOIN_RESPONSE_PORT);
		try {
			UDPServer.recieveObject(addr, JOIN_RESPONSE_PORT, 10000, JoinACK.class);
			DistributedHashTable dht = DistributedHashTable.getIntance();
			dht.setRight(addr);
			if (dht.getLeft() == null)
				dht.setLeft(addr);
			dht.checkNeighbor();
		} catch (SocketTimeoutException e) {
			throw new RuntimeException("Were not able to join acknowledgement", e);
		}
	}

	public static class JoinResponse implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String yourRightIP;

		public JoinResponse() {
			DistributedHashTable dh = DistributedHashTable.getIntance();
			if (dh.getRight() != null)
				yourRightIP = dh.getRight().getHostAddress();
		}

	}

	public static class JoinACK implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
}
