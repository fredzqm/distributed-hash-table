package dht_node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import networkUtility.ITCPConnectionListener;
import networkUtility.TCPServer;
import util.Lib;
import util.Logger;

public class DataTransfer implements ITCPConnectionListener {
	public static final int PORT = 4445;
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
			String path = Lib.readStr(input);
			Logger.logInfo("Looking for file %s", path);
			File file = new File(DistributedHashTable.DATA_LOCALTION + path);
			if (!file.exists()) {
				Logger.logInfo("File %s is not found", file.toPath());
				Lib.writeLong(0, output);
			} else {
				Logger.logInfo("File %s found, start transfering data", file.toPath());
				long fileSize = file.length();
				Lib.writeLong(fileSize, output);
				Lib.copyLarge(new FileInputStream(file), output);
			}
			input.close();
			output.close();
			conection.close();
			Logger.logInfo("Connection closed with %s", conection.getInetAddress());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
