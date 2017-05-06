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
		NodeInfo myself = dht.getMyself();
		if (myself == null) {
			Logger.logError("I am not initialized yet, please try again later after timed out");
			return;
		} else if (right != null) {
			// there is already more than two nodes in the cluster, ask the
			// right to update its left
			Sha256 sha = Sha256.middle(myself.getSha(), right.getSha());
			NodeInfo newNodeInfo = new NodeInfo(address, sha);
			dht.setRight(newNodeInfo);
			dht.sentMessage(new UpdateLeftRequest(newNodeInfo), right);
			dht.sentMessage(new JoinResponse(getRequestID(), myself, newNodeInfo, right), address);
		} else {
			// there is only one node in this cluster, just connects both left
			// and right to me
			Sha256 sha = Sha256.middle(myself.getSha(), myself.getSha());
			NodeInfo newNodeInfo = new NodeInfo(address, sha);
			dht.setRight(newNodeInfo);
			dht.setLeft(newNodeInfo);
			dht.sentMessage(new JoinResponse(getRequestID(), myself, newNodeInfo, myself), address);
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

		private final NodeInfo newLeftNode;

		public UpdateLeftRequest(NodeInfo newLeftNode) {
			super(0);
			this.newLeftNode = newLeftNode;
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			DistributedHashTable dht = DistributedHashTable.getIntance();
			dht.setLeft(newLeftNode);
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

		private final NodeInfo left, you, right;

		/**
		 * constructs an JoinRequest givens the requestID of corresponding join
		 * request
		 * 
		 * @param joinRequestID
		 * @param left
		 * @param myself
		 * @param right
		 */
		public JoinResponse(int joinRequestID, NodeInfo left, NodeInfo myself, NodeInfo right) {
			super(joinRequestID);
			this.left = left;
			this.you = myself;
			this.right = right;
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			DistributedHashTable dht = DistributedHashTable.getIntance();
			dht.setMySelf(this.you);
			dht.setLeft(this.left);
			dht.setRight(this.right);
			dht.sentMessage(new SimpleACKMessage(getRequestID()), address);
			dht.checkNeighbor();
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
			return String.format("JoinResponse left: %s\tyourself: %s\trightIP: %s\t%s", left, you, right,
					super.toString());
		}
	}

}
