package networkUtility;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import distributedHashTable.IDatagramPacketListener;

public class UDPServer implements Runnable {
	private final static int BUFFERSIZE = 1024;
	
	private IDatagramPacketListener listener;
	private Thread thread;
	private int port;
	private DatagramSocket socket;

	public UDPServer(int port, IDatagramPacketListener datagramPacketListener) {
		this.listener = datagramPacketListener;
		this.port = port;
	}

	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	@Override
	public void run() {
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		byte[] buffer = new byte[BUFFERSIZE];
		for (;;) {
			DatagramPacket packet = new DatagramPacket(buffer, BUFFERSIZE);
			try {
				socket.receive(packet);
				listener.onRecieved(packet);
			} catch (IOException e) {
				if (socket != null)
					socket.close();
				throw new RuntimeException(e);
			}
		}
	}

	public static void sendObject(Serializable object, InetAddress host, int port) {
		sendBytes(serializeObject(object), host, port);
	}

	public static void sendBytes(byte[] bytes, InetAddress host, int port) {
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
		DatagramSocket socker = null;
		try {
			socker = new DatagramSocket();
			socker.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socker != null)
				socker.close();
		}
	}

	public static <T> T recieveObject(InetAddress address, int port, int timeout, Class<T> clazz)
			throws SocketTimeoutException {
		return deSerializeObject(recieveBytes(address, port, timeout), clazz);
	}

	public static byte[] recieveBytes(InetAddress address, int port, int timeout) throws SocketTimeoutException {
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		DatagramPacket packet = new DatagramPacket(new byte[BUFFERSIZE], BUFFERSIZE);
		try {
			socket.setSoTimeout(timeout);
			socket.receive(packet);
			socket.setSoTimeout(0);
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (socket != null)
				socket.close();
		}
		if (!Arrays.equals(packet.getAddress().getAddress(), address.getAddress())) {
			throw new RuntimeException("RecievePacket From WrongIP");
		}
		return Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
	}

	public static byte[] serializeObject(Serializable object) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(byteArrayOutputStream);
			output.writeObject(object);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static <T> T deSerializeObject(byte[] bytes, Class<T> clazz) {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			Object object = inputStream.readObject();
			return clazz.cast(object);
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
	}

}
