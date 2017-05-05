package request;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import distributedHashTable.Settings;
import networkUtility.IDatagramPacketListener;
import networkUtility.UDPServer;

/**
 * The centralized communication handler for Our nodes Through this module, each
 * node can send and receive UDP {@link Message}. Each message needs to define
 * whether it requires acknowledgement {@link Message#requireACK()} If this is
 * true, then {@link CommunictionHandler} should tracks whether the ACK is
 * received, and trigger a timeout when necessary
 * 
 * @author fredzqm
 *
 */
public class CommunictionHandler implements IDatagramPacketListener, Runnable {
	private final int port;
	private UDPServer server;
	private Set<Integer> ackWaiting;
	private Random random;
	private Thread timeOutCheckThread;
	private PriorityQueue<UnACKeDMessage> unACKedMessages;

	/**
	 * constructs a communication handler at
	 * 
	 * @param port
	 */
	public CommunictionHandler(int port) {
		this.port = port;
		this.ackWaiting = new HashSet<>();
		this.unACKedMessages = new PriorityQueue<>();
		this.random = new Random();
		this.server = new UDPServer(port, this);
		this.server.start();
		this.timeOutCheckThread = new Thread(this);
		this.timeOutCheckThread.start();
	}
	
	@Override
	public void onRecieved(DatagramPacket packet) {
		InetAddress addr = packet.getAddress();
		Message request = UDPServer.deSerializeObject(packet.getData(), Message.class);
		if (Settings.isVerbose())
			System.out.println("[INFO] recieving message" + request);
		if (request.getACKID() != 0) {
			if (!ackWaiting.remove(request.getACKID()))
				System.out.println("[ERROR] recieved ack for request " + request.getACKID()
						+ " but is not in the ackWaiting pool");
		}
		request.handleRequest(addr);
	}

	/**
	 * send a message to an address. This message should be received by the
	 * {@link CommunictionHandler} there, and
	 * {@link Message#handleRequest(InetAddress)} should be called there
	 * 
	 * It this message requires ACK{@link Message#requireACK()}, and no ACK is
	 * received after certain time {@link Message#getTimeOut()},
	 * {@link Message#timeOut(InetAddress)} will be called
	 * 
	 * @param message
	 *            the message to be send
	 * @param address
	 *            the address to send it to
	 */
	public void sendMessage(Message message, InetAddress address) {
		if (message.requireACK()) {
			int requestID = generateUniqueRequestID();
			message.setRequestID(requestID);
			addToUnackedMessageQueue(new UnACKeDMessage(message, address));
		}
		if (Settings.isVerbose())
			System.out.println("[INFO] send message" + message);
		UDPServer.sendObject(message, address, port);
	}

	private synchronized int generateUniqueRequestID() {
		while (true) {
			int requestID = random.nextInt();
			if (!ackWaiting.contains(requestID) && requestID != 0) {
				ackWaiting.add(requestID);
				return requestID;
			}
		}
	}

	private synchronized void addToUnackedMessageQueue(UnACKeDMessage unACKedMessage) {
		boolean wakeUpTimeOutThread = this.unACKedMessages.isEmpty()
				|| unACKedMessage.getTime() < this.unACKedMessages.peek().getTime();
		this.unACKedMessages.add(unACKedMessage);
		if (wakeUpTimeOutThread) {
			this.notifyAll();
		}
	}

	@Override
	public synchronized void run() {
		while (true) {
			while (this.unACKedMessages.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long left = this.unACKedMessages.peek().getTime() - System.currentTimeMillis();
			if (left > 0) {
				try {
					this.wait(left);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (!this.unACKedMessages.isEmpty()) {
				UnACKeDMessage next = this.unACKedMessages.peek();
				if (!next.checkTimeOut(this.ackWaiting))
					break;
				this.unACKedMessages.poll();
			}
		}
	}

	private static class UnACKeDMessage implements Comparable<UnACKeDMessage> {
		private Message message;
		private long time;
		private InetAddress address;

		public UnACKeDMessage(Message message, InetAddress address) {
			this.message = message;
			this.address = address;
			this.time = System.currentTimeMillis() + message.getTimeOut();
		}

		/**
		 * 
		 * @param ackWaiting
		 * @return true if this message's time is up and processed and should be
		 *         removed from the queue, false if the time is not up yet
		 */
		public boolean checkTimeOut(Set<Integer> ackWaiting) {
			if (this.time < System.currentTimeMillis()) {
				if (ackWaiting.contains(this.message.getRequestID())) {
					this.message.timeOut(address);
				} else {
					this.message.acknowledge();
				}
				return true;
			}
			return false;
		}

		public long getTime() {
			return time;
		}

		@Override
		public int compareTo(UnACKeDMessage o) {
			return (int) (time - o.time);
		}

	}

}
