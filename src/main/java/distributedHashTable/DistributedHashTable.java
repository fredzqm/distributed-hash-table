package distributedHashTable;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import request.CommunictionHandler;
import request.Message;

public class DistributedHashTable {
	private static final int REQUEST_PARSER_PORT = 4444;

	// private Set<String> addresses;
	private Map<String, String> map;

	private CommunictionHandler requestParser;

	private InetAddress left;
	private InetAddress right;

	private DistributedHashTable() {
		// this.addresses = new HashSet<>();
		this.map = new HashMap<>();
		this.requestParser = new CommunictionHandler(REQUEST_PARSER_PORT);
	}

	public InetAddress getLeft() {
		return left;
	}

	public void setLeft(InetAddress left) {
		System.out.println("[INFO]: set left to be " + left.getHostName());
		this.left = left;
	}

	public InetAddress getRight() {
		return right;
	}

	public void setRight(InetAddress right) {
		System.out.println("[INFO]: set right to be " + right.getHostName());
		this.right = right;
	}

	public InetAddress getSide(boolean isRight) {
		return isRight ? getRight() : getLeft();
	}

	public void sentMessage(Message message, InetAddress address) {
		this.requestParser.sendMessage(message, address);
	}

	public void joinCluster(InetAddress entryNode) throws SocketTimeoutException, UnknownHostException {
		JoinRequest joinRequest = new JoinRequest();
		DistributedHashTable.getIntance().sentMessage(joinRequest, entryNode);
	}

	public String get(String fileName) {
		if (map.containsKey(fileName)) {
			return map.get(fileName);
		}
		return null;
	}

	public String put(String fileName, String content) {
		if (map.containsKey(fileName)) {
			return "File already exists!";
		}
		map.put(fileName, content);
		return "File successfully added!";
	}

	public String remove(String fileName) {
		return map.remove(fileName);
	}

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

	public synchronized void checkNeighbor(boolean reachingRight) {
		if (reachingRight) {
			if (Settings.isVerbose())
				System.out.println("Checking right");
			if (right == null)
				throw new RuntimeException("[ERROR] right is null");
			CheckNeighborRequest forRight = new CheckNeighborRequest(true);
			sentMessage(forRight, right);
		} else {
			if (Settings.isVerbose())
				System.out.println("Checking left");
			if (left == null)
				throw new RuntimeException("[ERROR] left is null");
			CheckNeighborRequest forLeft = new CheckNeighborRequest(false);
			sentMessage(forLeft, left);
		}
	}

	public synchronized void checkNeighbor() {
		checkNeighbor(true);
		checkNeighbor(false);
	}

	@Override
	public String toString() {
		return String.format("Left: %s, Right: %s", left.getHostAddress(), right.getHostAddress());
	}

}
