package distributedHashTable;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import networkUtility.UDPServer;
import request.CheckAliveMessage;
import request.JoinRequest;

public class DistributedHashTable {
	private Set<String> addresses;
	private Map<String, String> map;

	private RequestParser requestParser;

	private InetAddress left;
	private InetAddress right;

	private DistributedHashTable() {
		this.addresses = new HashSet<>();
		this.map = new HashMap<>();
		this.requestParser = new RequestParser();
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

	public void joinCluster(InetAddress entryNode) throws SocketTimeoutException, UnknownHostException {
		JoinRequest.sendJoinRequest(entryNode);
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

	public void checkNeighbor() {
		System.out.println("Checking neigher");
		UDPServer.sendObject(new CheckAliveMessage(), right, RequestParser.PORT);
		UDPServer.sendObject(new CheckAliveMessage(), left, RequestParser.PORT);
		try {
			System.out.println("Checking waiting for response");
			UDPServer.recieveBytes(right, CheckAliveMessage.CHECK_ALIVE_ACK_PORT, 1000);
			UDPServer.recieveBytes(left, CheckAliveMessage.CHECK_ALIVE_ACK_PORT, 1000);
			System.out.println("[INFO]: Both neight are up, left: " + this.left.getHostAddress() + " right: "
					+ this.right.getHostAddress());
		} catch (SocketTimeoutException e) {
			System.err.println("checkNeighbor timed out");
			checkNeighbor();
//			throw new RuntimeException(e);
		}
	}

}
