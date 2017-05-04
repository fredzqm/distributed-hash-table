package distributedHashTable;
import java.net.DatagramPacket;

public interface IDatagramPacketListener {
	
	void onRecieved(DatagramPacket packet);
}
