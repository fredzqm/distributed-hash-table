package dht_client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
			try {
				socket = new Socket(address, DataTransfer.PORT);
				Logger.logInfo("Connection started with node %s", address);
				InputStream input = socket.getInputStream();
				OutputStream output = socket.getOutputStream();
				output.write(DataTransfer.GET);
				Lib.writeStr(output, key);
				long fileLength = Lib.readLong(input);
				if (fileLength != 0) {
					Logger.logProgress("The length of file %s is %d", key, fileLength);
					callback.onGetInputStream(input);
				} else {
					Logger.logProgress("key %s is not found", key);
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

	public void put(String key, PutCallback callback) {
		Search findRequest = new Search(key, ips, (address) -> {
			Socket socket = null;
			try {
				socket = new Socket(address, DataTransfer.PORT);
				Logger.logInfo("Connection started with node %s", address);
				InputStream input = socket.getInputStream();
				OutputStream output = socket.getOutputStream();
				output.write(DataTransfer.PUT);
				Lib.writeStr(output, key);
				long response = Lib.readLong(input);
				if (response != 0) {
					Logger.logProgress("The file %s is already exists %d bytes long", key, response);
					callback.onGetOuputStream(null);
				} else {
					Logger.logProgress("Waiting for data to transimit");
					callback.onGetOuputStream(output);
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

	public interface PutCallback {
		/**
		 * The input stream constains the data of this file
		 * 
		 * @param output
		 *            null if this file already exists
		 */
		void onGetOuputStream(OutputStream output);
	}

	public void contains(String key, ContainCallback callback) {
		Search findRequest = new Search(key, ips, (address) -> {
			Socket socket = null;
			try {
				socket = new Socket(address, DataTransfer.PORT);
				Logger.logInfo("Connection started with node %s", address);
				InputStream input = socket.getInputStream();
				OutputStream output = socket.getOutputStream();
				output.write(DataTransfer.CONTAINS);
				Lib.writeStr(output, key);
				long response = Lib.readLong(input);
				if (response != 0) {
					Logger.logProgress("The file %s is already exists %d bytes long", key, response);
					callback.onHasFile(false);
				} else {
					Logger.logProgress("The file %s does not exists", key);
					callback.onHasFile(true);
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

	public interface ContainCallback {
		/**
		 * The input stream constains the data of this file
		 * 
		 * @param has
		 *            true if this file exists in this DHT
		 */
		void onHasFile(boolean has);

	}
	
	public void delete(String key, ContainCallback callback) {
		Search findRequest = new Search(key, ips, (address) -> {
			Socket socket = null;
			try {
				socket = new Socket(address, DataTransfer.PORT);
				Logger.logInfo("Connection started with node %s", address);
				InputStream input = socket.getInputStream();
				OutputStream output = socket.getOutputStream();
				output.write(DataTransfer.DELETE);
				Lib.writeStr(output, key);
				long response = Lib.readLong(input);
				if (response != 0) {
					Logger.logProgress("The file %s is already exists %d bytes long", key, response);
					callback.onHasFile(false);
				} else {
					Logger.logProgress("The file %s does not exists", key);
					callback.onHasFile(true);
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
}
