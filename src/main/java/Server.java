import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private final int bufferSize;
	private final List<IDatagramPacketListener> listeners;
	private final DatagramSocket socket;

	public Server(int port, int bufferSize) throws SocketException {
		this.listeners = new ArrayList<>();
		this.socket = new DatagramSocket(port);
		this.bufferSize = bufferSize;
	}

	public void addListener(RequestParser datagramListener) {
		this.listeners.add(datagramListener);
	}

	public void startServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				byte[] buffer = new byte[bufferSize];
				for (;;) {
					DatagramPacket packet = new DatagramPacket(buffer, bufferSize);
					try {
						socket.receive(packet);
						for (IDatagramPacketListener d : listeners) {
							d.onRecieved(packet);
						}
					} catch (IOException e) {
						if (socket != null)
							socket.close();
						throw new RuntimeException(e);
					}
				}
			}

		}).start();
	}
	
	public static void sendObject(Object object, InetAddress host, int port) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(byteArrayOutputStream);
			output.writeObject(object);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		sendBytes(byteArrayOutputStream.toByteArray(), host, port);
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

}
