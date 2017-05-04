package distributedHashTable;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import request.CheckAliveMessage;
import request.JoinRequest;
import request.Message;

public class DistributedHashTable {
	private Set<String> addresses;
	private Map<String, String> map;

	private CommunictionHandler requestParser;

	private InetAddress left;
	private InetAddress right;

	private DistributedHashTable() {
		this.addresses = new HashSet<>();
		this.map = new HashMap<>();
		this.requestParser = new CommunictionHandler();
	}

	public InetAddress getLeft() {
		return left;
	}

	public void setLeft(InetAddress left) {
		this.left = left;
	}

	public InetAddress getRight() {
		return right;
	}

	public void setRight(InetAddress right) {
		this.right = right;
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

	public synchronized void checkNeighbor() {
		System.out.println("Checking neigher");
		if (right == null)
			throw new RuntimeException("[ERROR] right is null");
		CheckAliveMessage forRight = new CheckAliveMessage("Right");
		sentMessage(forRight, right);
		if (left == null)
			throw new RuntimeException("[ERROR] left is null");
		CheckAliveMessage forLeft = new CheckAliveMessage("Left");
		sentMessage(forLeft, left);
	}

	@Override
	public String toString() {
		return String.format("Left: %s, Right: %s", left.getHostAddress(), right.getHostAddress());
	}

}
