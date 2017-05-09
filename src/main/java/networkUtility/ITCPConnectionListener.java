package networkUtility;

import java.io.IOException;
import java.net.Socket;

/**
 * 
 * InputStream in = client.getInputStream(); OutputStream out =
 * client.getOutputStream(); String ip =
 * client.getInetAddress().getHostAddress();
 */
public interface ITCPConnectionListener {

	/**
	 * consume the connection
	 * 
	 * @param connection
	 * @throws IOException 
	 */
	void handleConnection(Socket connection) throws IOException;

}
