package distributedHashTable;

import java.net.InetAddress;

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
		NodeInfo right = dht.getRight();
		Sha256 shaThis = dht.getSha();
		if (right != null) {
			// there is already more than two nodes in the cluster, ask the
			Sha256 shaRight = right.getSha();
			// right to update its left
			Sha256 sha = Sha256.middle(shaThis, shaRight);
			dht.sentMessage(new UpdateLeftRequest(address.getHostAddress(), sha), dht.getRight().getAddress());
			dht.sentMessage(new JoinResponse(getRequestID(), null, dht.getRight(), sha), address);
			dht.setRight(new NodeInfo(address, sha));
		} else {
			// there is only one node in this cluster, just connects both left
			// and right to me
			Sha256 sha = Sha256.middle(shaThis, shaThis);
			dht.sentMessage(new JoinResponse(getRequestID(), null, null, sha), address);
			dht.setRight(new NodeInfo(address, sha));
			dht.setLeft(new NodeInfo(address, sha));
		}
		dht.checkNeighbor();
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
		private Sha256 newLeftSha;

		public UpdateLeftRequest(String newLeft, Sha256 newLeftSha) {
			super(0);
			this.newLeft = newLeft;
			this.newLeftSha = newLeftSha;
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			DistributedHashTable dht = DistributedHashTable.getIntance();
			dht.setLeft(new NodeInfo(this.newLeft, this.newLeftSha));
			dht.sentMessage(new SimpleACKMessage(getRequestID()), address);
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
		private Sha256 yourLeftSha;
		private String yourRightIP;
		private Sha256 yourRightSha;
		private Sha256 yourSha;
		private Sha256 mySha;

		/**
		 * constructs an JoinRequest givens the requestID of corresponding join
		 * request
		 * 
		 * @param joinRequestID
		 * @param sha
		 */
		public JoinResponse(int joinRequestID, NodeInfo left, NodeInfo right, Sha256 sha) {
			super(joinRequestID);
			if (left != null) {
				this.yourLeftIP = left.getHostAddress();
				this.yourLeftSha = left.getSha();
			}
			if (right != null) {
				this.yourRightIP = right.getHostAddress();
				this.yourLeftSha = right.getSha();
			}
			this.yourSha = sha;
			this.mySha = DistributedHashTable.getIntance().getSha();
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			DistributedHashTable dht = DistributedHashTable.getIntance();
			dht.setMySha(yourSha);
			if (this.yourRightIP != null)
				dht.setRight(new NodeInfo(this.yourRightIP, this.yourRightSha));
			else
				dht.setRight(new NodeInfo(address, this.mySha));
			if (this.yourLeftIP != null)
				dht.setLeft(new NodeInfo(this.yourLeftIP, this.yourLeftSha));
			else
				dht.setLeft(new NodeInfo(address, this.mySha));
			dht.checkNeighbor();
			dht.sentMessage(new SimpleACKMessage(getRequestID()), address);
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

		@Override
		public String toString() {
			return "JoinResponse leftIP: " + yourLeftIP + " rightIP: " + yourRightIP + "\t" + super.toString();
		}
	}

}
