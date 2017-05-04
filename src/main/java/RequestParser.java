import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class RequestParser implements IDatagramPacketListener {
	public static final int PORT = 3000;
	private final static boolean VERBOSE = true;
	private UDPServer server;

	public RequestParser()  {
		this.server = new UDPServer(PORT, this);
		this.server.start();
	}

	@Override
	public void onRecieved(DatagramPacket packet) throws IOException {
		InetAddress addr = packet.getAddress();
		Request request = UDPServer.deSerializeObject(packet.getData(), Request.class);
		if (VERBOSE)
			System.out.println(request);
		request.handleRequest(addr);
	}

}
