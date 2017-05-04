package networkUtility;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {
	private final ITCPConnectionListener listener;

	private Thread thread;

	private final int port;

	public TCPServer(int port, ITCPConnectionListener datagramPacketListener) {
		this.port = port;
		this.listener = datagramPacketListener;
		this.thread = new Thread(this);
	}

	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	@Override
	public void run() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket();
			socket.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		while (true) {
			try {
				Socket client = socket.accept();
				new Thread(()->{
					listener.handleConnection(client);
				}).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
