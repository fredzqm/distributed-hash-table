import java.io.Serializable;
import java.net.InetAddress;

public interface Request extends Serializable {
	void handleRequest(InetAddress addr);
}
