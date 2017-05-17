package dht_node;

import java.net.InetAddress;

import networkUtility.Timer;
import request.ACK;
import request.CommunicationHandler;
import request.Message;
import util.Logger;
import util.NodeInfo;

public class CircularMessage extends Message implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static long timeOutThreshold = 1000;

	private static CircularMessage lastRecieved;
	private static long lastTimeCheck;
	private static long numOfNode;

	private NodeInfo yourLeft;
	private NodeInfo lonelyNode;
	private int messageId;
	private long count;

	public CircularMessage(NodeInfo lonelyNode) {
		super(0);
		this.count = 0;
		this.messageId = (int) (Math.random() * Integer.MAX_VALUE) + 1;
		this.lonelyNode = lonelyNode;
	}

	@Override
	protected CircularMessage clone() {
		try {
			return (CircularMessage) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		// send ACK
		CommunicationHandler.sendMessage(new ACK(getRequestID()), address);
		// store this message
		CircularMessage.lastRecieved = this.clone();
		if (lastRecieved != null && this.messageId == lastRecieved.messageId) {
			// the message go throughs a circle
			CircularMessage.numOfNode = this.count - lastRecieved.count + 1;
		} else {
			// the previous message gets lost, this is a new one
			CircularMessage.numOfNode = 0;
		}
		CircularMessage.lastTimeCheck = System.currentTimeMillis();
		this.count++;
		send();
	}

	public boolean requireACK() {
		return lonelyNode != null;
	}

	@Override
	public long getTimeOut() {
		return 100;
	}

	@Override
	public void timeOut(InetAddress address) {
		System.out.println("My right is not responding, rewiring it to the lonely node");
		DistributedHashTable.getIntance().setRight(lonelyNode);
		send();
	}

	public void send() {
		this.yourLeft = DistributedHashTable.getIntance().getMyself();
		NodeInfo right = DistributedHashTable.getIntance().getRight();
		if (right == null) {
			Logger.logProgress("Right not initialized yet");
			return;
		}
		CommunicationHandler.sendMessage(this, right.getAddress());
	}

	public static boolean isActive() {
		return lastRecieved != null && timeSinceHearFromLeft() < timeOutThreshold;
	}

	private static long timeSinceHearFromLeft() {
		return System.currentTimeMillis() - lastTimeCheck;
	}

	public static NodeInfo getLeft() {
		if (!isActive())
			return null;
		return lastRecieved.yourLeft;
	}

	public static void checkCircle() {
		Timer.setTimeOut(timeOutThreshold, () -> {
			if (isActive()) {
				if (numOfNode != 0) {
					Logger.logProgress("I am active, checked %d ago, left is %s, %d node", timeSinceHearFromLeft(),
							getLeft(), numOfNode);
				} else {
					Logger.logProgress("I am active, checked %d ago, left is %s, but circle is broken",
							timeSinceHearFromLeft(), getLeft());
				}
			} else {
				if (DistributedHashTable.getIntance().getMyself() == null) {
					throw new RuntimeException("Has not defined myself yet");
				} else {
					if (timeSinceHearFromLeft() > timeOutThreshold * 3) {
						Logger.logProgress("I am not active and lonely now, sending a circular message");
						CircularMessage circular = new CircularMessage(DistributedHashTable.getIntance().getMyself());
						circular.send();
					} else {
						Logger.logProgress("I am not active now, sending a circular message");
						CircularMessage circular = new CircularMessage(null);
						circular.send();
					}
				}
			}
			checkCircle();
		});
	}

}
