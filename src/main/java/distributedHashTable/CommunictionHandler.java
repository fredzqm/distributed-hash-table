package distributedHashTable;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import networkUtility.UDPServer;
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
		if (request.getACKID() != 0) {
			if (!ackWaiting.remove(request.getACKID()))
				System.out.println("[ERROR] recieved ack for request " + request.getACKID()
						+ " but is not in the ackWaiting pool");
		}
		request.handleRequest(addr);
	}

	public void sendMessage(Message request, InetAddress address) {
		if (request.requireACK()) {
			int requestID = generateUniqueRequestID();
			request.setRequestID(requestID);
			(new Thread(() -> {
				try {
					Thread.sleep(request.getTimeOut());
					if (ackWaiting.contains(ackWaiting)) {
						request.timeOut(address);
					} else {
						request.acknowledge();
					}
				} catch (InterruptedException e) {
					System.err.println("Waiting for " + request + " is interrrupted, trigger timout");
					request.timeOut(address);
				}
			})).start();
		}
		if (Settings.VERBOSE)
			System.out.println("[INFO] send message" + request);
		UDPServer.sendObject(request, address, PORT);
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
