package request;

import java.net.InetAddress;

import distributedHashTable.DistributedHashTable;

public class CheckAliveMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int CHECK_ALIVE_ACK_PORT = 3332;
	private transient boolean timeOuted;

	public CheckAliveMessage() {
		timeOuted = false;
	}

	@Override
	public void handleRequest(InetAddress addr) {
		DistributedHashTable.getIntance().sentMessage(new AliveACK(this.getRequestID()), addr);
	}

	@Override
	public long getTimeOut() {
		return 1000;
	}

	@Override
	public void timeOut(InetAddress address) {
		timeOuted = true;
	}
	
	public boolean isTimeOut() {
		return timeOuted;
	}

	public class AliveACK extends Message {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AliveACK(int ackForID) {
			super(ackForID);
		}

		@Override
		public void handleRequest(InetAddress addr) {
			// do nothing
		}

		@Override
		public long getTimeOut() {
			return 0;
		}

		@Override
		public void timeOut(InetAddress address) {
			
		}

		@Override
		public boolean requireACK() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Override
	public boolean requireACK() {
		// TODO Auto-generated method stub
		return false;
	}

}
