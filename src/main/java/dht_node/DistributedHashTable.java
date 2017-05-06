package dht_node;

import java.net.InetAddress;

import request.CommunicationHandler;
import util.Logger;
import util.NodeInfo;

public class DistributedHashTable {

	// private Set<String> addresses;
	// private Map<String, String> map;
	private NodeInfo myself, left, right;

	private DistributedHashTable() {
		// this.addresses = new HashSet<>();
		// this.map = new HashMap<>();
		CommunicationHandler.getInstance().start();
	}

	public NodeInfo getLeft() {
		return left;
	}

	public void setLeft(NodeInfo left) {
		Logger.logInfo("set left to be %s", left);
		this.left = left;
	}

	public NodeInfo getRight() {
		return right;
	}

	public void setRight(NodeInfo right) {
		Logger.logInfo("set right to be %s", right);
		this.right = right;
	}

	public NodeInfo getMyself() {
		return myself;
	}

	public void setMySelf(NodeInfo myself) {
		if (this.myself != null)
			throw new RuntimeException("You cannot redefine myself " + myself);
		Logger.logInfo("set myself to be %s", myself);
		this.myself = myself;
	}

	/**
	 * 
	 * @param isRight
	 * @return the {@link InetAddress} at certain side
	 */
	public NodeInfo getSide(boolean isRight) {
		return isRight ? getRight() : getLeft();
	}

	/**
	 * Attempting to join the cluster the entry node is now in
	 * 
	 * @param entryNode
	 */
	public void joinCluster(InetAddress entryNode) {
		JoinRequest joinRequest = new JoinRequest();
		CommunicationHandler.sendMessage(joinRequest, entryNode);
	}

	public synchronized void checkNeighbor(boolean reachingRight) {
		if (reachingRight) {
			if (right == null)
				throw new RuntimeException("[ERROR] right is null");
			Logger.logInfo("[INFO] Checking right: %s", right);
			CheckNeighborRequest forRight = new CheckNeighborRequest(true, myself, right);
			CommunicationHandler.sendMessage(forRight, right.getAddress());
		} else {
			if (left == null)
				throw new RuntimeException("[ERROR] left is null");
			Logger.logInfo("[INFO] Checking left: %s", left);
			CheckNeighborRequest forLeft = new CheckNeighborRequest(false,myself, left);
			CommunicationHandler.sendMessage(forLeft, left.getAddress());
		}
	}

	public void checkNeighbor() {
		checkNeighbor(true);
		checkNeighbor(false);
	}

	@Override
	public String toString() {
		return String.format("Left: %s, Right: %s", left, right);
	}

	// public String get(String fileName) {
	// if (map.containsKey(fileName)) {
	// return map.get(fileName);
	// }
	// return null;
	// }
	//
	// public String put(String fileName, String content) {
	// if (map.containsKey(fileName)) {
	// return "File already exists!";
	// }
	// map.put(fileName, content);
	// return "File successfully added!";
	// }
	//
	// public String remove(String fileName) {
	// return map.remove(fileName);
	// }
	//
	private static DistributedHashTable table;

	public static DistributedHashTable getIntance() {
		if (table == null) {
			synchronized (DistributedHashTable.class) {
				if (table == null) {
					table = new DistributedHashTable();
				}
			}
		}
		return table;
	}
	
}
