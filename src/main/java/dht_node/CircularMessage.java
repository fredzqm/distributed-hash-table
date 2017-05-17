package dht_node;

import java.net.InetAddress;

import networkUtility.Timer;
import request.AbstractACKMessage;
import request.CommunicationHandler;
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
	private static long lastTimeCheck;
	private static long lastCount;
	private static long numOfNode;

	private NodeInfo yourLeft;
	private long count;

	public CircularMessage(NodeInfo yourLeft) {
		super(0);
		this.yourLeft = yourLeft;
		count = 0;
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		CircularMessage.left = this.yourLeft;
		CircularMessage.numOfNode = this.count - CircularMessage.lastCount;
		CircularMessage.lastCount = this.count;
//		CircularMessage.timeOutThreshold = 100 * (System.currentTimeMillis() - CircularMessage.lastTimeCheck);
		CircularMessage.lastTimeCheck = System.currentTimeMillis();
		this.count++;
		this.yourLeft = DistributedHashTable.getIntance().getMyself();
		send();
	}

	public void send() {
		NodeInfo right = DistributedHashTable.getIntance().getRight();
		if (right == null) {
			Logger.logProgress("Right not initialized yet");
			return;
		}
		CommunicationHandler.sendMessage(this, right.getAddress());
	}

	public static boolean isActive() {
		return System.currentTimeMillis() - lastTimeCheck < timeOutThreshold;
	}

	public static NodeInfo getLeft() {
		return left;
	}

	public static void checkCircle() {
		Timer.setTimeOut(timeOutThreshold, () -> {
			if (isActive()) {
				Logger.logProgress("I am active, checked %d ago, left is %s, %d node",
						System.currentTimeMillis() - lastTimeCheck, left, numOfNode);
			} else {
				Logger.logProgress("I am not active now, sending a circular message");
				NodeInfo myself = DistributedHashTable.getIntance().getMyself();
				if (myself == null) {
					throw new RuntimeException("Has not defined myself yet");
				} else {
					CircularMessage circular = new CircularMessage(myself);
					circular.send();
				}
			}
			checkCircle();
		});
	}

}
