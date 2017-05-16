package dht_node;

import java.net.InetAddress;

import request.AbstractACKMessage;
import request.CommunicationHandler;
import request.Message;
import util.Logger;
import util.NodeInfo;

/**
 * The message to check if the a certain node is alive
 * 
 * @author fredzqm
 *
 */
public class CheckNeighborRequest extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final boolean reachingRight;
	private final NodeInfo me, you;

	/**
	 * creates a checkAliveMessage
	 * 
	 * @param right
	 */
	public CheckNeighborRequest(boolean isRight, NodeInfo me, NodeInfo you) {
		super(0);
		this.reachingRight = isRight;
		this.me = me;
		this.you = you;
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		NodeInfo self = dht.getMyself();
		if (self == null) {
			CommunicationHandler.sendMessage(
					new CheckAliveNAK(this.getRequestID(), Logger.logError("This node is not yet initialized")),
					address);
		} else if (!self.equals(this.you)) {
			CommunicationHandler.sendMessage(new CheckAliveNAK(this.getRequestID(),
					Logger.logInfo("This node is %s, but recognized as %s", self, this.you)), address);
		} else {
			String side = getSideStr(!this.reachingRight);
			NodeInfo correspondSide = dht.getSide(!this.reachingRight);
			if (correspondSide == null) {
				CommunicationHandler.sendMessage(
						new CheckAliveNAK(this.getRequestID(), Logger.logInfo("%s is not yet initialized", side)),
						address);
			} else if (!correspondSide.equals(this.me)) {
				CommunicationHandler.sendMessage(
						new CheckAliveNAK(this.getRequestID(),
								Logger.logInfo("Thought %s is %s, %s is actually %s", side, correspondSide, this.me)),
						address);
			} else {
				CommunicationHandler.sendMessage(new CheckAliveACK(this.getRequestID()), address);
			}
		}
	}

	@Override
	public long getTimeOut() {
		return 1000;
	}

	@Override
	public void timeOut(InetAddress address) {
		System.err.println("[ERROR] " + getSideStr(reachingRight) + " is not responding");
		CommunicationHandler.sendMessage(this, address);
	}

	@Override
	public String toString() {
		return super.toString() + String.format(" | Reaching %s", getSideStr(reachingRight));
	}

	public static String getSideStr(boolean isRight) {
		return isRight ? "right" : "left";
	}

	public static class CheckAliveNAK extends AbstractACKMessage {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String message;

		public CheckAliveNAK(int ackForID, String message) {
			super(ackForID);
			this.message = message;
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			CheckNeighborRequest checkAliveMessage = (CheckNeighborRequest) acknowleged;
			Logger.logInfo("%s is not properly wired -- %s", getSideStr(checkAliveMessage.reachingRight), message);
			DistributedHashTable.getIntance().checkNeighbor(checkAliveMessage.reachingRight);
		}

	}

	public static class CheckAliveACK extends AbstractACKMessage {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CheckAliveACK(int ackForID) {
			super(ackForID);
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			CheckNeighborRequest checkAliveMessage = (CheckNeighborRequest) acknowleged;
			DistributedHashTable dht = DistributedHashTable.getIntance();
			Logger.logProgress("I am %s, my %s is %s", dht.getMyself(),
					getSideStr(checkAliveMessage.reachingRight), dht.getSide(checkAliveMessage.reachingRight));
		}

	}

}
