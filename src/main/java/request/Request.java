package request;
import java.io.Serializable;
import java.net.InetAddress;

/**
 * 
 * @author fredzqm
 *
 */
public interface Request extends Serializable {
	
	/**
	 * 
	 * @param addr
	 */
	void handleRequest(InetAddress addr);
}
