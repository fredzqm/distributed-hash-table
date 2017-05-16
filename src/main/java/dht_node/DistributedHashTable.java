package dht_node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import dht_client.DataTransfer;
import networkUtility.Timer;
import request.CommunicationHandler;
import util.Logger;
import util.NodeInfo;

public class DistributedHashTable {
	public static final String DATA_LOCALTION = "data/";
	public static final String TABLE_LOCATION = DATA_LOCALTION + "meta.properties";

	private Map<String, String> map;
	private NodeInfo myself, left, right;
	private DataTransfer dataTransfer;

	private DistributedHashTable() {
		CommunicationHandler.getInstance().start();
		dataTransfer = new DataTransfer(this);
		checkNeighbor();
	}

	private void loadTable() {
		map = new HashMap<String, String>();
		Properties properties = new Properties();
		File table = new File(TABLE_LOCATION);
		try {
			table.createNewFile();
			FileInputStream in = new FileInputStream(table);
			properties.load(in);
			for (String key : properties.stringPropertyNames()) {
				map.put(key, properties.get(key).toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeTable() {
		Properties properties = new Properties();
		properties.putAll(map);
		File table = new File(TABLE_LOCATION);
		try {
			table.createNewFile();
			FileOutputStream out = new FileOutputStream(table);
			properties.store(out, null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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

	private void checkNeighbor() {
		Logger.logInfo("Set check neighbor timeout");
		Timer.setTimeOut(2000, () -> {
			Logger.logInfo("Called check neighbor timeout");
			if (right == null)
				Logger.logProgress("right is null");
			else {
				Logger.logInfo("Checking right: %s", right);
				CheckNeighborRequest forRight = new CheckNeighborRequest(true, myself, right);
				CommunicationHandler.sendMessage(forRight, right.getAddress());
			}
			if (left == null) {
				Logger.logProgress("left is null");
			} else {
				Logger.logInfo("Checking left: %s", left);
				CheckNeighborRequest forLeft = new CheckNeighborRequest(false, myself, left);
				CommunicationHandler.sendMessage(forLeft, left.getAddress());
			}
			checkNeighbor();
		});
	}

	public void brokenConnectionTo(boolean reachingRight) {
		Logger.logError("Connection with %s is broken", CheckNeighborRequest.getSideStr(reachingRight));
	}

	@Override
	public String toString() {
		return String.format("Left: %s, Right: %s", left, right);
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

}
