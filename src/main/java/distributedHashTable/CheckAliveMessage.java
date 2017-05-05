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
public class CheckAliveMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient String nameOfHostChecked;
	private transient int times;

	/**
	 * creates a checkAliveMessage
	 */
	public CheckAliveMessage(String nameOfHostChecked) {
		super(0);
		this.nameOfHostChecked = nameOfHostChecked;
		this.times = 0;
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		DistributedHashTable.getIntance().sentMessage(new CheckAliveACK(this.getRequestID()), address);
	}

	@Override
	public long getTimeOut() {
		return 100;
	}

	@Override
	public void timeOut(InetAddress address) {
		System.err.println("[ERROR] " + nameOfHostChecked + " is not responding -- " + times);
		times++;
		DistributedHashTable.getIntance().sentMessage(this, address);
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
			CheckAliveMessage checkAliveMessage = (CheckAliveMessage) acknowleged;
			System.out.println("[INFO] " + checkAliveMessage.nameOfHostChecked + " is up running");
		}

	}

}
