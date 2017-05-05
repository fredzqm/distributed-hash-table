package distributedHashTable;

import java.net.InetAddress;
import java.net.UnknownHostException;

import request.AbstractACKMessage;
import request.Message;

/**
 * The request to join a cluster
 * 
 * @author fredzqm
 *
 */
public class JoinRequest extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * creates a Join Request. Since it initialize a request, it does not
	 * acknowledge any previous message
	 */
	public JoinRequest() {
		super(0);
	}

	public static int JOIN_RESPONSE_PORT = 3330;

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		DistributedHashTable.getIntance().sentMessage(new JoinResponse(getRequestID()), address);
	}

	@Override
	public long getTimeOut() {
		return 2000;
	}

	@Override
	public void timeOut(InetAddress address) {
		System.err.println("[ERROR] JoinRequest waiting for join response timed out, resending join request");
		DistributedHashTable.getIntance().sentMessage(new JoinResponse(getRequestID()), address);
	}

	/**
	 * The response to {@link JoinRequest}
	 * 
	 * @author fredzqm
	 *
	 */
	public static class JoinResponse extends Message {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String yourRightIP;

		/**
		 * constructs an JoinRequest givens the requestID of corresponding join
		 * request
		 * 
		 * @param joinRequestID
		 */
		public JoinResponse(int joinRequestID) {
			super(joinRequestID);
			DistributedHashTable dh = DistributedHashTable.getIntance();
			if (dh.getRight() != null)
				yourRightIP = dh.getRight().getHostAddress();
		}

		@Override
		public void handleRequest(InetAddress entryNode, Message acknowleged) {
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
			dht.sentMessage(new JoinACK(getRequestID()), entryNode);
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

		public class JoinACK extends AbstractACKMessage {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public JoinACK(int requestID) {
				super(requestID);
			}

			@Override
			public void handleRequest(InetAddress address, Message acknowleged) {
				DistributedHashTable dht = DistributedHashTable.getIntance();
				dht.setRight(address);
				if (dht.getLeft() == null) // there is only two nodes
					dht.setLeft(address);
				DistributedHashTable.getIntance().checkNeighbor();
			}
		}

	}

}
