import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	public static final int PORT = 3000;
	private final static int PACKETSIZE = 100;
	private List<DatagramPacket> packets;

	public Server() {
		this.packets = new ArrayList<>();
	}

	public void startServer() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				DatagramSocket socket = null;
				try {
					socket = new DatagramSocket(PORT);

					for (;;) {
						DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
						socket.receive(packet);
						packets.add(packet);
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}).start();
	}
	
}
