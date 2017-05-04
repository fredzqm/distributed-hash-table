import java.net.Socket;

/**
 * 
 * InputStream in = client.getInputStream(); OutputStream out =
 * client.getOutputStream(); String ip =
 * client.getInetAddress().getHostAddress();
 */
public interface ITCPConnectionListener {

	void handleConnection(Socket client);

}
