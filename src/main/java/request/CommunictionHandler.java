package request;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashSet;
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
public class CommunictionHandler implements IDatagramPacketListener {
	public static final int PORT = 4444;
	private UDPServer server;
	private Set<Integer> ackWaiting;
	private Random random;

	public CommunictionHandler() {
		this.ackWaiting = new HashSet<>();
		this.random = new Random();
		this.server = new UDPServer(PORT, this);
		this.server.start();
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
			(new Thread(() -> {
				try {
					Thread.sleep(message.getTimeOut());
					if (ackWaiting.contains(ackWaiting)) {
						message.timeOut(address);
					} else {
						message.acknowledge();
					}
				} catch (InterruptedException e) {
					System.err.println("Waiting for " + message + " is interrrupted, trigger timout");
					message.timeOut(address);
				}
			})).start();
		}
		if (Settings.isVerbose())
			System.out.println("[INFO] send message" + message);
		UDPServer.sendObject(message, address, PORT);
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

}
