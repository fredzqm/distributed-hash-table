package distributedHashTable;

import java.net.InetAddress;
import java.net.UnknownHostException;

import request.CommunictionHandler;
import request.Message;

public class DistributedHashTable {
	private static final int REQUEST_PARSER_PORT = 4444;

	// private Set<String> addresses;
	// private Map<String, String> map;

	private CommunictionHandler requestParser;

	private NodeInfo myself, left, right;

	private DistributedHashTable() {
		// this.addresses = new HashSet<>();
		// this.map = new HashMap<>();
		this.requestParser = new CommunictionHandler(REQUEST_PARSER_PORT);
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

	public void setMySha(Sha256 sha) {
		try {
			setMySelf(new NodeInfo(InetAddress.getLocalHost(), sha));
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public void setMySelf(NodeInfo you) {
		if (this.myself != null)
			throw new RuntimeException("You cannot redefine myself " + you);
		Logger.logInfo("set myself to be %s", you);
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
	 * Send a message via {@link CommunictionHandler}
	 * 
	 * @param message
	 *            message
	 * @param address
	 *            the {@link InetAddress} address to send to
	 */
	public void sentMessage(Message message, InetAddress address) {
		this.requestParser.sendMessage(message, address);
	}

	public void sentMessage(Message message, NodeInfo node) {
		sentMessage(message, node.getAddress());
	}

	/**
	 * Attempting to join the cluster the entry node is now in
	 * 
	 * @param entryNode
	 */
	public void joinCluster(InetAddress entryNode) {
		JoinRequest joinRequest = new JoinRequest();
		DistributedHashTable.getIntance().sentMessage(joinRequest, entryNode);
	}

	public synchronized void checkNeighbor(boolean reachingRight) {
		if (reachingRight) {
			if (right == null)
				throw new RuntimeException("[ERROR] right is null");
			Logger.logInfo("[INFO] Checking right: %s", right);
			CheckNeighborRequest forRight = new CheckNeighborRequest(true, myself, right);
			sentMessage(forRight, right.getAddress());
		} else {
			if (left == null)
				throw new RuntimeException("[ERROR] left is null");
			Logger.logInfo("[INFO] Checking left: %s", left);
			CheckNeighborRequest forLeft = new CheckNeighborRequest(false,myself, left);
			sentMessage(forLeft, left.getAddress());
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
