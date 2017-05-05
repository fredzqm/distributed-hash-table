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

/**
 * The UDP Server that emits UDP packets to {@link IDatagramPacketListener}
 * 
 * @author fredzqm
 *
 */
public class UDPServer implements Runnable {
	private final static int BUFFERSIZE = 1024;

	private IDatagramPacketListener listener;
	private Thread thread;
	private int port;
	private DatagramSocket socket;

	/**
	 * 
	 * @param port
	 *            the port this UDPServer would listen at
	 * @param datagramPacketListener
	 *            the listener who will consume the UDP datagrams
	 */

	public UDPServer(int port, IDatagramPacketListener datagramPacketListener) {
		this.listener = datagramPacketListener;
		this.port = port;
	}

	/**
	 * start the server
	 */
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
		while (true) {
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

	/**
	 * Send a UDP object to certain host at a port
	 * 
	 * @param object
	 * @param host
	 * @param port
	 */
	public static void sendObject(Serializable object, InetAddress host, int port) {
		sendBytes(serializeObject(object), host, port);
	}

	/**
	 * send certain bytes to a certain host at a port
	 * 
	 * @param bytes
	 * @param host
	 * @param port
	 */
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

	/**
	 * serialize java object into byte array
	 * 
	 * @param object
	 * @return
	 */
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

	/**
	 * deserialized byte array into java object
	 * 
	 * @param bytes
	 * @param clazz
	 * @return
	 */
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
