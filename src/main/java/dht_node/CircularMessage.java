package dht_node;

import java.net.InetAddress;

import networkUtility.Timer;
import request.AbstractACKMessage;
import request.Message;
import util.Logger;
import util.NodeInfo;

public class CircularMessage extends AbstractACKMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static NodeInfo left;
	private static long timeOutThreshold = 1000;
	private static long lastTimeCheck = System.currentTimeMillis();

	private NodeInfo yourLeft;
	private long count;

	public CircularMessage(NodeInfo yourLeft) {
		super(0);
		this.yourLeft = yourLeft;
		count = 0;
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		CircularMessage.left = this.yourLeft;
		this.lastTimeCheck = System.currentTimeMillis();
		count++;
		send();
	}

	public void send() {

	}

	public static boolean isActive() {
		return System.currentTimeMillis() - lastTimeCheck < timeOutThreshold;
	}

	public static NodeInfo getLeft() {
		return left;
	}

	public static void checkCircle() {
		Timer.setTimeOut(timeOutThreshold, () -> {
			NodeInfo myself = DistributedHashTable.getIntance().getMyself();
			if (myself == null) {
				Logger.logError("Has not defined myself yet");
			} else {
				CircularMessage circular = new CircularMessage(myself);
				circular.send();
			}
		});
	}
}
