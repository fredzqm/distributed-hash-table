import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
						socket.close();
						throw new RuntimeException(e);
					}
				}
			}

		}).start();
	}


}
