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
	private final Sha256 sha;

	public NodeInfo(InetAddress address, Sha256 sha) {
		this.address = address;
		this.sha = sha;
	}

	public NodeInfo(String IP, Sha256 sha) {
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

	public Sha256 getSha() {
		return sha;
	}

	public String getHostAddress() {
		return address.getHostAddress();
	}
}
