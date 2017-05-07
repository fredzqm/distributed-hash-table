package dht_client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import dht_client.Client.GetResponse;
import dht_node.DataTransfer;
import request.CommunicationHandler;
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
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				output.writeObject(new Request(key));
				output.flush();
				GetResponse response = (GetResponse) input.readObject();
				if (response.hasObject()) {
					Logger.logInfo("key %s is found", key);
					callback.onGetInputStream(input);
				} else {
					Logger.logInfo("key %s is not found", key);
					callback.onGetInputStream(null);
				}
				Logger.logInfo("Connection closed with node %s", address);
			} catch (IOException | ClassNotFoundException e) {
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

	public static class GetResponse implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private boolean hasFile;

		public GetResponse(boolean hasFile) {
			this.hasFile = hasFile;
		}

		public boolean hasObject() {
			return hasFile;
		}

	}

	public static class Request implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String key;

		public Request(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

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
