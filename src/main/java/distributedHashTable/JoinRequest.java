package distributedHashTable;

import java.net.InetAddress;
import java.net.UnknownHostException;

import request.AbstractACKMessage;
import request.Message;
import request.SimpleACKMessage;

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

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		DistributedHashTable dht = DistributedHashTable.getIntance();

		// TODO: in the future actually figure out a proper position to
		// insert it. Right now just insert at the right side
		if (dht.getRight() != null) {
			// there is already more than two nodes in the cluster, ask the
			// right to update its left
			dht.sentMessage(new UpdateLeftRequest(address.getHostAddress()), dht.getRight());
			dht.sentMessage(new JoinResponse(getRequestID(), null, dht.getRight().getHostAddress()), address);
		} else {
			// there is only one node in this cluster, just connects both left
			// and right to me
			dht.sentMessage(new JoinResponse(getRequestID(), null, null), address);
		}
	}

	@Override
	public long getTimeOut() {
		return 2000;
	}

	@Override
	public void timeOut(InetAddress address) {
		System.err.println("[ERROR] JoinRequest waiting for join response timed out, resending join request");
		DistributedHashTable.getIntance().sentMessage(this, address);
	}

	public class UpdateLeftRequest extends Message {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String newLeft;

		public UpdateLeftRequest(String newLeft) {
			super(0);
			this.newLeft = newLeft;
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			DistributedHashTable dht = DistributedHashTable.getIntance();
			dht.sentMessage(new SimpleACKMessage(getRequestID()), address);
			try {
				dht.setLeft(InetAddress.getByName(this.newLeft));
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
			dht.checkNeighbor();
		}

		@Override
		public long getTimeOut() {
			return 1000;
		}

		@Override
		public void timeOut(InetAddress address) {
			System.err.println("[ERROR] UpdateLeftRequest timedout");
			DistributedHashTable.getIntance().sentMessage(this, address);
		}

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

		private String yourLeftIP;
		private String yourRightIP;

		/**
		 * constructs an JoinRequest givens the requestID of corresponding join
		 * request
		 * 
		 * @param joinRequestID
		 */
		public JoinResponse(int joinRequestID, String leftIP, String rightIP) {
			super(joinRequestID);
			this.yourLeftIP = leftIP;
			this.yourRightIP = rightIP;
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			DistributedHashTable dht = DistributedHashTable.getIntance();
			if (this.yourRightIP != null)
				try {
					dht.setRight(InetAddress.getByName(this.yourRightIP));
				} catch (UnknownHostException e) {
					throw new RuntimeException(e);
				}
			else
				dht.setRight(address);
			if (this.yourLeftIP != null)
				try {
					dht.setLeft(InetAddress.getByName(this.yourLeftIP));
				} catch (UnknownHostException e) {
					throw new RuntimeException(e);
				}
			else
				dht.setLeft(address);
			dht.checkNeighbor();
			dht.sentMessage(new JoinACK(getRequestID()), address);
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

	public static class JoinACK extends AbstractACKMessage {
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
			dht.checkNeighbor();
		}
	}

}
