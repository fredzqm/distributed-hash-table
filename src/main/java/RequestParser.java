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
		byte[] data = packet.getData();
		int port = packet.getLength();
		ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(data));
		try {
			Request message = (Request) inputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
