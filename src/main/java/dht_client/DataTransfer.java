package dht_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import dht_node.DistributedHashTable;
import networkUtility.ITCPConnectionListener;
import networkUtility.TCPServer;
import util.Lib;
import util.Logger;

public class DataTransfer implements ITCPConnectionListener {
	public static final int PORT = 4445;
	public static final int GET = 0;
	public static final int PUT = 1;
	public static final int DELETE = 2;
	public static final int CONTAINS = 3;

	private TCPServer tcpServer;
	private DistributedHashTable dht;

	public DataTransfer(DistributedHashTable distributedHashTable) {
		this.dht = distributedHashTable;
		tcpServer = new TCPServer(PORT, this);
		tcpServer.start();
	}

	@Override
	public void handleConnection(Socket conection) {
		try {
			Logger.logInfo("Connection started with %s", conection.getInetAddress());
			InputStream input = conection.getInputStream();
			OutputStream output = conection.getOutputStream();
			int operation = input.read();
			switch (operation) {
			case GET:
				get(input, output);
				break;
			case PUT:
				put(input, output);
				break;
			case CONTAINS:
				contains(input, output);
				break;
			case DELETE:
				delete(input, output);
				break;
			default:
				throw new RuntimeException("Unrecognized operation type " + operation);
			}
			input.close();
			output.close();
			conection.close();
			Logger.logInfo("Connection closed with %s", conection.getInetAddress());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void get(InputStream input, OutputStream output) throws IOException, FileNotFoundException {
		Logger.logProgress("Get Operation");
		String key = Lib.readStr(input);
		Logger.logInfo("Get operation key is %s", key);
		File file = new File(getFileNameFromKey(key));
		if (!file.exists()) {
			Logger.logProgress("File %s is not found", file.toPath());
			Lib.writeLong(0, output);
		} else {
			Logger.logProgress("File %s found, start transfering data", file.toPath());
			long fileSize = file.length();
			Lib.writeLong(fileSize, output);
			Lib.copyLarge(new FileInputStream(file), output);
		}
	}

	private void put(InputStream input, OutputStream output) throws IOException, FileNotFoundException {
		String key = Lib.readStr(input);
		Logger.logInfo("key is %s", key);
		File file = new File(getFileNameFromKey(key));
		if (!file.exists()) {
			Logger.logProgress("File %s does not exist", file.toPath());
			Lib.writeLong(0, output);
			OutputStream fileOuput = new FileOutputStream(file);
			Logger.logProgress("start receiving data");
			Lib.copyLarge(input, fileOuput);
			Logger.logProgress("finish receiving data");
			fileOuput.close();
		} else {
			Lib.writeLong(file.length(), output);
			Logger.logProgress("File %s are ready exists put operation failed", file.toPath());
		}
	}
	
	private void contains(InputStream input, OutputStream output) throws IOException, FileNotFoundException {
		String key = Lib.readStr(input);
		Logger.logInfo("key is %s", key);
		File file = new File(getFileNameFromKey(key));
		if (!file.exists()) {
			Lib.writeLong(0, output);
			Logger.logProgress("File %s does not exist", file.toPath());
		} else {
			Lib.writeLong(file.length(), output);
			Logger.logProgress("File %s are ready exists put operation failed", file.toPath());
		}
	}
	
	private void delete(InputStream input, OutputStream output) throws IOException, FileNotFoundException {
		String key = Lib.readStr(input);
		Logger.logInfo("key is %s", key);
		File file = new File(getFileNameFromKey(key));
		if (!file.exists()) {
			Lib.writeLong(0, output);
			Logger.logProgress("File %s does not exist", file.toPath());
		} else {
			Lib.writeLong(file.length(), output);
			Logger.logProgress("File %s are ready exists put operation failed", file.toPath());
			file.delete();
		}
	}

	private String getFileNameFromKey(String key) {
		return DistributedHashTable.DATA_LOCALTION + key;
	}
}
