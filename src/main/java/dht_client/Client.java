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

	public void get(String key, IGetCallback callback) {
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

	public interface IGetCallback {
		/**
		 * The input stream constains the data of this file
		 * 
		 * @param input
		 *            null if this file is not in the dht
		 */
		void onGetInputStream(InputStream input);
	}

	/**
	 * The synchronized version of get
	 * 
	 * @param key
	 * @return
	 */
	public InputStream get(String key) {
		GetCallback callback = new GetCallback();
		get(key, callback);
		try {
			callback.wait();
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
		return callback.input;
	}

	private static class GetCallback implements IGetCallback {
		public InputStream input;

		public void onGetInputStream(InputStream input) {
			this.input = input;
			this.notifyAll();
		}
	}

	public void put(String key, IPutCallback callback) {
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

	public interface IPutCallback {
		/**
		 * The input stream constains the data of this file
		 * 
		 * @param output
		 *            null if this file already exists
		 */
		void onGetOuputStream(OutputStream output);
	}

	/**
	 * The synchronized version of put
	 * 
	 * @param key
	 * @return
	 */
	public OutputStream put(String key) {
		PutCallback callback = new PutCallback();
		put(key, callback);
		try {
			callback.wait();
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
		return callback.output;
	}

	private static class PutCallback implements IPutCallback {
		public OutputStream output;

		public void onGetOuputStream(OutputStream output) {
			this.output = output;
			this.notifyAll();
		}
	}

	public void contains(String key, IContainCallback callback) {
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
					callback.onHasFile(true);
				} else {
					Logger.logProgress("The file %s does not exists", key);
					callback.onHasFile(false);
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

	public interface IContainCallback {
		/**
		 * The input stream constains the data of this file
		 * 
		 * @param has
		 *            true if this file exists in this DHT
		 */
		void onHasFile(boolean has);

	}


	/**
	 * The synchronized version of contains
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(String key) {
		ContainCallback callback = new ContainCallback();
		contains(key, callback);
		try {
			callback.wait();
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
		return callback.has;
	}

	private static class ContainCallback implements IContainCallback {
		public boolean has;

		public void onHasFile(boolean has) {
			this.has = has;
			this.notifyAll();
		}
	}
	
	public void delete(String key, IContainCallback callback) {
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
					callback.onHasFile(true);
				} else {
					Logger.logProgress("The file %s does not exists", key);
					callback.onHasFile(false);
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
	
	/**
	 * The synchronized version of delete
	 * 
	 * @param key
	 * @return
	 */
	public boolean delete(String key) {
		ContainCallback callback = new ContainCallback();
		delete(key, callback);
		try {
			callback.wait();
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
		return callback.has;
	}
}
