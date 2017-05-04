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
import java.util.Arrays;

public class Server implements Runnable {
	private final int bufferSize;
	private final IDatagramPacketListener listener;
	private final DatagramSocket socket;
	private final Thread thread;

	public Server(int port, int bufferSize, IDatagramPacketListener datagramPacketListener) throws SocketException {
		this.listener = datagramPacketListener;
		this.socket = new DatagramSocket(port);
		this.bufferSize = bufferSize;
		this.thread = new Thread(this);
	}

	public void start() {
		this.thread.start();
	}

	@Override
	public void run() {
		byte[] buffer = new byte[bufferSize];
		for (;;) {
			DatagramPacket packet = new DatagramPacket(buffer, bufferSize);
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

	public static byte[] recieveBytes(InetAddress address, int port, int timeout, int bufferSize) {
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(port);
			socket.setSoTimeout(timeout);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (socket != null)
				socket.close();
		}
		if (!Arrays.equals(packet.getAddress().getAddress(), address.getAddress())) {
			throw new RecievePacketFromWrongIPExcpetion();
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
