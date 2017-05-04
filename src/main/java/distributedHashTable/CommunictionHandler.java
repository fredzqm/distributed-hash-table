package distributedHashTable;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import networkUtility.UDPServer;
import request.AbstractRequest;
import request.Message;

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
		if (Settings.VERBOSE)
			System.out.println("[INFO] recieving message" + request);
		request.handleRequest(addr);
	}

	public void sendMessage(Message message, InetAddress address) {
		if (message instanceof AbstractRequest) {
			AbstractRequest request = (AbstractRequest) message;
			int requestID = generateUniqueRequestID();
			request.setRequestID(requestID);
			(new Thread(() -> {
				try {
					Thread.sleep(request.getTimeOut());
					if (ackWaiting.contains(ackWaiting)) {
						request.timeOut();
					}
				} catch (InterruptedException e) {
					System.err.println("Waiting for " + request + " is interrrupted, trigger timout");
					request.timeOut();
				}
			})).start();
		}
		if (Settings.VERBOSE)
			System.out.println("[INFO] send message" + message);
		UDPServer.sendObject(message, address, PORT);
	}

	private synchronized int generateUniqueRequestID() {
		while (true) {
			int requestID = random.nextInt();
			if (!ackWaiting.contains(requestID)) {
				ackWaiting.add(requestID);
				return requestID;
			}
		}
	}
	
}
