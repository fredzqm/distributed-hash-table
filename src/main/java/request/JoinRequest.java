package request;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import distributedHashTable.CommunictionHandler;
import distributedHashTable.DistributedHashTable;
import networkUtility.UDPServer;

public class JoinRequest implements Message {
	public static int JOIN_RESPONSE_PORT = 3330;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void sendJoinRequest(InetAddress entryNode) throws SocketTimeoutException, UnknownHostException {
		JoinRequest joinRequest = new JoinRequest();
		UDPServer.sendObject(joinRequest, entryNode, CommunictionHandler.PORT);
		JoinResponse response = UDPServer.recieveObject(entryNode, JOIN_RESPONSE_PORT, 10000, JoinResponse.class);
		DistributedHashTable dht = DistributedHashTable.getIntance();
		if (response.yourRightIP != null)
			dht.setRight(InetAddress.getByName(response.yourRightIP));
		else
			dht.setRight(entryNode);
		dht.setLeft(entryNode);
		System.out.println(dht);
		dht.checkNeighbor();
		UDPServer.sendObject(new JoinACK(), entryNode, JOIN_RESPONSE_PORT);
	}

	@Override
	public void handleRequest(InetAddress addr) {
		UDPServer.sendObject(new JoinResponse(), addr, JOIN_RESPONSE_PORT);
		try {
			UDPServer.recieveObject(addr, JOIN_RESPONSE_PORT, 10000, JoinACK.class);
			DistributedHashTable dht = DistributedHashTable.getIntance();
			dht.setRight(addr);
			if (dht.getLeft() == null)
				dht.setLeft(addr);
			dht.checkNeighbor();
		} catch (SocketTimeoutException e) {
			System.err.println("[ERROR] waiting for Joint request timed out");
			handleRequest(addr);
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
