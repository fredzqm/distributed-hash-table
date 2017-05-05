package distributedHashTable;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Contains information about a remote node
 * 
 * @author fredzqm
 *
 */
public class NodeInfo {
	private final InetAddress address;
	private final String sha;

	public NodeInfo(InetAddress address, String sha) {
		this.address = address;
		this.sha = sha;
	}

	public NodeInfo(String IP, String sha) {
		try {
			this.address = InetAddress.getByName(IP);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Cannot resolve IP address", e);
		}
		this.sha = sha;
	}

	public InetAddress getAddress() {
		return address;
	}

	public String getSha() {
		return sha;
	}

	public String getHostAddress() {
		return address.getHostAddress();
	}
}
