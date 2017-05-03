import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class RequestParser implements IDatagramPacketListener {
	public static final int PORT = 3000;
	private final static int PACKETSIZE = 100;
	private Server server;

	public RequestParser() throws SocketException {
		this.server = new Server(PORT, PACKETSIZE);
		this.server.addListener(this);
	}

	@Override
	public void onRecieved(DatagramPacket packet) throws IOException {
		InetAddress addr = packet.getAddress();
		ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream( packet.getData()));
		try {
			Request request = (Request) inputStream.readObject();
			request.handleRequest(addr);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
