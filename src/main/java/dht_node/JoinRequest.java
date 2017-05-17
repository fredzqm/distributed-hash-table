package dht_node;

import java.net.InetAddress;

import request.ACK;
import request.CommunicationHandler;
import request.Message;
import util.Logger;
import util.NodeInfo;
import util.Sha256;

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
		} else {
			Sha256 sha = Sha256.middle(myself.getSha(), myself.getSha());
			NodeInfo newNodeInfo = new NodeInfo(address, sha);
			dht.setRight(newNodeInfo);
			CommunicationHandler.sendMessage(new JoinResponse(getRequestID(), newNodeInfo, right != null? right: myself), address);
		}
	}

	@Override
	public long getTimeOut() {
		return 2000;
	}

	@Override
	public void timeOut(InetAddress address) {
		System.err.println("[ERROR] JoinRequest waiting for join response timed out, resending join request");
		CommunicationHandler.sendMessage(this, address);
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

		private final NodeInfo you, right;

		/**
		 * constructs an JoinRequest givens the requestID of corresponding join
		 * request
		 * 
		 * @param joinRequestID
		 * @param myself
		 * @param right
		 */
		public JoinResponse(int joinRequestID, NodeInfo myself, NodeInfo right) {
			super(joinRequestID);
			this.you = myself;
			this.right = right;
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			DistributedHashTable dht = DistributedHashTable.getIntance();
			dht.setMySelf(this.you);
			dht.setRight(this.right);
			CommunicationHandler.sendMessage(new ACK(getRequestID()), address);
		}

		@Override
		public long getTimeOut() {
			return 1000;
		}

		@Override
		public void timeOut(InetAddress address) {
			System.err.println("[ERROR] JoinResponse timed out");
			CommunicationHandler.sendMessage(this, address);
		}

		@Override
		public String toString() {
			return super.toString() + String.format(" | JoinResponse you: %s\tright: %s\t", you, right);
		}
	}

}
