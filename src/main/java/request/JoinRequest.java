package request;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.accessibility.AccessibleKeyBinding;

import distributedHashTable.DistributedHashTable;
import networkUtility.UDPServer;

public class JoinRequest extends AbstractRequest {
	public static int JOIN_RESPONSE_PORT = 3330;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void handleRequest(InetAddress addr) {
		DistributedHashTable.getIntance().sentMessage(new JoinResponse(), addr);
		// try {
		// UDPServer.recieveObject(addr, JOIN_RESPONSE_PORT, 10000,
		// JoinACK.class);
		// DistributedHashTable dht = DistributedHashTable.getIntance();
		// dht.setRight(addr);
		// if (dht.getLeft() == null)
		// dht.setLeft(addr);
		// dht.checkNeighbor();
		// } catch (SocketTimeoutException e) {
		// System.err.println("[ERROR] waiting for Joint request timed out");
		// handleRequest(addr);
		// }
	}

	@Override
	public long getTimeOut() {
		return 2000;
	}

	@Override
	public void timeOut(InetAddress address) {
		System.err.println("[ERROR] JoinRequest waiting for join response timed out, resending join request");
		DistributedHashTable.getIntance().sentMessage(new JoinResponse(), address);
	}

	public static class JoinResponse extends AbstractRequest {

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

		@Override
		public void handleRequest(InetAddress entryNode) {
			new Thread(() -> {
				DistributedHashTable dht = DistributedHashTable.getIntance();
				if (this.yourRightIP != null)
					try {
						dht.setRight(InetAddress.getByName(this.yourRightIP));
					} catch (UnknownHostException e) {
						throw new RuntimeException(e);
					}
				else
					dht.setRight(entryNode);
				dht.setLeft(entryNode);
				dht.checkNeighbor();
				DistributedHashTable.getIntance().sentMessage(new JoinACK(), entryNode);
			}).start();
		}

		@Override
		public long getTimeOut() {
			return 1000;
		}

		@Override
		public void timeOut(InetAddress address) {
			System.err.println("[ERROR] JoinResponse timed out");
			DistributedHashTable.getIntance().sentMessage(this, address);
		}

	}

	public static class JoinACK implements AbstractACK {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void handleRequest(InetAddress addr) {

		}

		@Override
		public int getRequestID() {
			return 0;
		}

	}

}
