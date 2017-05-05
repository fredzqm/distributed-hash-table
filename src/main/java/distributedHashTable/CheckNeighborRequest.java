package distributedHashTable;

import java.net.InetAddress;

import request.AbstractACKMessage;
import request.Message;

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

	private boolean reachingRight;

	/**
	 * creates a checkAliveMessage
	 */
	public CheckNeighborRequest(boolean isRight) {
		super(0);
		this.reachingRight = isRight;
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		InetAddress correspondSide = dht.getSideAddress(!this.reachingRight);
		if (correspondSide == null || !address.getHostAddress().equals(correspondSide.getHostAddress())) {
			dht.sentMessage(new CheckAliveNAK(this.getRequestID()), address);
		} else {
			dht.sentMessage(new CheckAliveACK(this.getRequestID()), address);
		}
	}

	@Override
	public long getTimeOut() {
		return 1000;
	}

	@Override
	public void timeOut(InetAddress address) {
		System.err.println("[ERROR] " + getSideStr(reachingRight) + " is not responding");
		DistributedHashTable.getIntance().sentMessage(this, address);
	}

	@Override
	public String toString() {
		return String.format("Reaching %s\t%s", getSideStr(reachingRight), super.toString());
	}

	public static String getSideStr(boolean isRight) {
		return isRight ? "right" : "left";
	}

	public class CheckAliveNAK extends AbstractACKMessage {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CheckAliveNAK(int ackForID) {
			super(ackForID);
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			CheckNeighborRequest checkAliveMessage = (CheckNeighborRequest) acknowleged;
			Logger.logInfo("%s is not properly wired", getSideStr(checkAliveMessage.reachingRight));
			DistributedHashTable.getIntance().checkNeighbor(checkAliveMessage.reachingRight);
		}

	}

	public class CheckAliveACK extends AbstractACKMessage {
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
			Logger.logProgress("%s  is properly wired with %s", getSideStr(checkAliveMessage.reachingRight),
					address.getHostAddress());
		}

	}

}
