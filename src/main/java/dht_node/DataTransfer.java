package dht_node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dht_client.Client;
import dht_client.Client.Request;
import networkUtility.ITCPConnectionListener;
import networkUtility.TCPServer;
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
			ObjectInputStream input = new ObjectInputStream(new LogInputStream(conection.getInputStream()));
			ObjectOutputStream output = new ObjectOutputStream(new LogOuputStream(conection.getOutputStream()));
			Request request = (Request) input.readObject();
			File file = new File(request.getKey());
			if (file.exists()) {
				Logger.logInfo("File %s is not found", file.toPath());
				output.writeObject(new Client.GetResponse(false));
			} else {
				Logger.logInfo("File %s found, start transfering data", file.toPath());
				output.writeObject(new Client.GetResponse(true));
				Logger.copyLarge(new FileInputStream(file), output);
			}
			Logger.logInfo("Connection closed with %s", conection.getInetAddress());
			input.close();
			output.close();
			conection.close();
		} catch (IOException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}

}
