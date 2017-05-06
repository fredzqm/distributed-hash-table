package distributedHashTable;

import java.net.InetAddress;
import java.util.Objects;

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
	private Sha256 storedSha;

	/**
	 * creates a checkAliveMessage
	 */
	public CheckNeighborRequest(boolean isRight, Sha256 storedSha) {
		super(0);
		this.reachingRight = isRight;
		this.storedSha = storedSha;
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		String side = getSideStr(!this.reachingRight);
		NodeInfo correspondSide = dht.getSide(!this.reachingRight);
		if (correspondSide == null) {
			Logger.logInfo("%s address mismatch now %s but should %s", side, "null", correspondSide.getHostAddress());
		} else if (!address.getHostAddress().equals(correspondSide.getHostAddress())) {
			Logger.logInfo("%s address mismatch now %s but should %s", side, address.getHostAddress(),
					correspondSide.getHostAddress());
			dht.sentMessage(new CheckAliveNAK(this.getRequestID()), address);
		} else if (!Objects.equals(this.storedSha, dht.getSha())) {
			Logger.logInfo("%s sha mismatch now %s but should %s", side, dht.getSha(), this.storedSha);
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
