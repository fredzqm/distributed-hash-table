import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class RequestParser implements IDatagramPacketListener {
	public static final int PORT = 3000;
	private UDPServer server;

	public RequestParser() throws SocketException {
		this.server = new UDPServer(PORT, this);
		this.server.start();
	}

	@Override
	public void onRecieved(DatagramPacket packet) throws IOException {
		InetAddress addr = packet.getAddress();
		Request request = UDPServer.deSerializeObject(packet.getData(), Request.class);
		request.handleRequest(addr);
	}

}
