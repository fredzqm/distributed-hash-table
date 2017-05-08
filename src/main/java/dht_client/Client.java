package dht_client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import dht_node.DataTransfer;
import request.CommunicationHandler;
import util.Lib;
import util.Logger;

public class Client {

	private List<String> ips;

	public Client() {
		this.ips = new ArrayList<>();
		CommunicationHandler.getInstance().start();
	}

	public void addWokerNodeIP(String ip) {
		this.ips.add(ip);
	}

	public void get(String key, GetCallback callback) {
		Search findRequest = new Search(key, ips, (address) -> {
			Socket socket = null;
			Logger.logInfo("Here %s", address);
			try {
				Logger.logInfo("Connection started with node %s", address);
				socket = new Socket(address, DataTransfer.PORT);
				InputStream input = socket.getInputStream();
				OutputStream output = socket.getOutputStream();
				Lib.writeStr(output, key);
				long response = Lib.readLong(input);
				if (response != 0) {
					Logger.logInfo("The length of file %s is %d", key, response);
					callback.onGetInputStream(input);
				} else {
					Logger.logInfo("key %s is not found", key);
					callback.onGetInputStream(null);
				}
				Logger.logInfo("Connection closed with node %s", address);
			} catch (IOException e) {
				e.printStackTrace();
				Logger.logError("TCP connection failed to establish with %s", address.getHostAddress());
				return false;
			} finally {
				if (socket != null)
					try {
						socket.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
			}
			return true;
		});
		findRequest.send();
	}

	public interface GetCallback {
		/**
		 * The input stream constains the data of this file
		 * 
		 * @param input
		 *            null if this file is not in the dht
		 */
		void onGetInputStream(InputStream input);
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
