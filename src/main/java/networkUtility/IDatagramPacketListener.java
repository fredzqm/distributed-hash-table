package networkUtility;

import java.net.DatagramPacket;

/**
 * 
 * The listener interface for {@link UDPServer}. It would receive the
 * {@link DatagramPacket} every time something arrives
 * 
 * @author fredzqm
 *
 */
public interface IDatagramPacketListener {

	/**
	 * 
	 * @param packet
	 *            the datagram packet
	 */
	void onRecieved(DatagramPacket packet);
}
