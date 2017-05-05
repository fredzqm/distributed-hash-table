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

	private transient boolean isRight;
	private transient int times;

	/**
	 * creates a checkAliveMessage
	 */
	public CheckNeighborRequest(boolean isRight) {
		super(0);
		this.isRight = !isRight;
		this.times = 0;
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		InetAddress correspondSide = this.isRight ? dht.getRight() : dht.getRight();
		if (correspondSide == null || address.getHostAddress().equals(correspondSide.getHostAddress()))
			dht.sentMessage(new CheckAliveACK(this.getRequestID()), address);
		else
			dht.sentMessage(new CheckAliveNAK(this.getRequestID()), address);
	}

	@Override
	public long getTimeOut() {
		return 100;
	}

	@Override
	public void timeOut(InetAddress address) {
		System.err.println("[ERROR] " + isRight + " is not responding -- " + times);
		times++;
		DistributedHashTable.getIntance().sentMessage(this, address);
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
			System.out.println("[INFO] " + (checkAliveMessage.isRight ? "Right" : "Left") + " is not properly wired");
			acknowleged.timeOut(address);
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
			System.out.println("[INFO] " + (checkAliveMessage.isRight ? "Right" : "Left") + " is properly wired");
		}

	}

}
