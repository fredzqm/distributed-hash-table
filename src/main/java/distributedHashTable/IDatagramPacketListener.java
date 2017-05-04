package distributedHashTable;
import java.io.IOException;
import java.net.DatagramPacket;

public interface IDatagramPacketListener {
	
	void onRecieved(DatagramPacket packet) throws IOException;
}
