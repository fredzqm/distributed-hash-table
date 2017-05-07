package request;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import networkUtility.IDatagramPacketListener;
import networkUtility.Timer;
import networkUtility.UDPServer;
import util.Logger;

/**
 * The centralized communication handler for Our nodes Through this module, each
 * node can send and receive UDP {@link Message}. Each message needs to define
 * whether it requires acknowledgement {@link Message#requireACK()} If this is
 * true, then {@link CommunicationHandler} should tracks whether the ACK is
 * received, and trigger a timeout when necessary
 * 
 * @author fredzqm
 *
 */
public class CommunicationHandler implements IDatagramPacketListener {
	public static final int REQUEST_PARSER_PORT = 4444;

	private static CommunicationHandler instance;

	private final int PORT;
	private UDPServer server;
	private Map<Integer, Message> ackWaiting;
	private int lastTime;

	/**
	 * constructs a communication handler at
	 * 
	 * @param PORT
	 */
	private CommunicationHandler() {
		this.PORT = REQUEST_PARSER_PORT;
		this.ackWaiting = new ConcurrentHashMap<>();
		this.lastTime = 1;
		this.server = new UDPServer(PORT, this);
	}

	/**
	 * start to listen at port {@link CommunicationHandler#PORT}
	 */
	public void start() {
		this.server.start();
	}

	public static CommunicationHandler getInstance() {
		if (instance == null) {
			synchronized (CommunicationHandler.class) {
				if (instance == null) {
					instance = new CommunicationHandler();
				}
			}
		}
		return instance;
	}

	@Override
	public void onRecieved(DatagramPacket packet) {
		InetAddress addr = packet.getAddress();
		Message request = UDPServer.deSerializeObject(packet.getData(), Message.class);
		Logger.logInfo("recv %s from %s", request, packet.getAddress().getHostAddress());
		Message acknowledged = null;
		if (request.getACKID() != 0) {
			acknowledged = ackWaiting.remove(request.getACKID());
			if (acknowledged == null)
				Logger.logError("recieved ack for requestID %d but is not in the ackWaiting pool", request.getACKID());
			if (request.getACKID() - lastTime > 0)
				lastTime = request.getACKID();
		}
		request.handleRequest(addr, acknowledged);
	}

	/**
	 * send a message to an address. This message should be received by the
	 * {@link CommunicationHandler} there, and
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
	public static void sendMessage(Message message, InetAddress address) {
		getInstance()._sendMessage(message, address);
	}

	/**
	 * Same as {@link CommunicationHandler#sendMessage(Message, InetAddress)}
	 * excepts it takes an IP and convert it into {@link InetAddress}
	 * 
	 * @param message
	 * @param IP
	 */
	public static void sendMessage(Message message, String IP) {
		try {
			sendMessage(message, InetAddress.getByName(IP));
		} catch (UnknownHostException e) {
			Logger.logError("IP %s was not found when sending messages", IP);
			e.printStackTrace();
		}
	}

	private void _sendMessage(Message message, InetAddress address) {
		if (message.requireACK()) {
			addToACKQueue(message, address);
		}
		Logger.logInfo("send %s to %s", message, address.getHostAddress());
		UDPServer.sendObject(message, address, PORT);
	}

	private void addToACKQueue(Message message, InetAddress address) {
		lastTime++;
		if (lastTime == 0)
			lastTime = 1;
		final int requestID = lastTime;
		message.setRequestID(requestID);
		this.ackWaiting.put(requestID, message);
		Timer.setTimeOut(message.getTimeOut(), () -> {
			if (ackWaiting.containsKey(requestID)) {
				message.timeOut(address);
			}
		});
	}

}
