package networkUtility;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The TCP Server that listens on certain port emits a connection to
 * {@link ITCPConnectionListener}when someone tries to connect
 * 
 * @author fredzqm
 *
 */
public class TCPServer implements Runnable {
	private final ITCPConnectionListener listener;
	private final int port;
	private Thread thread;

	/**
	 * 
	 * @param port
	 *            the port this TCPServer would listen at
	 * @param tcpConnectionlitener
	 *            the listener who will consume the connected sockets
	 */
	public TCPServer(int port, ITCPConnectionListener tcpConnectionlitener) {
		this.port = port;
		this.listener = tcpConnectionlitener;
		this.thread = new Thread(this);
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
				new Thread(() -> {
					listener.handleConnection(client);
				}).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
