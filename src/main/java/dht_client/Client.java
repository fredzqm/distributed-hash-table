package dht_client;

import java.util.ArrayList;
import java.util.List;

import request.CommunicationHandler;

public class Client {
	private List<String> ips;

	public Client() {
		this.ips = new ArrayList<>();
		CommunicationHandler.getInstance().start();
	}

	public void addWokerNodeIP(String ip) {
		this.ips.add(ip);
	}

	public void get(String path) {
		Search findRequest = new Search(path, ips);
		findRequest.send();
	}

	// private static Client table;
	// public static Client getIntance() {
	// if (table == null) {
	// synchronized (DistributedHashTable.class) {
	// if (table == null) {
	// table = new Client();
	// }
	// }
	// }
	// return table;
	// }
}
