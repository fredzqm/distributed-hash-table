package distributedHashTable;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Contains information about a remote node
 * 
 * @author fredzqm
 *
 */
public class NodeInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NodeInfo) {
			NodeInfo o = (NodeInfo) obj;
			return this.getHostAddress().equals(o.getHostAddress()) && this.sha.equals(o.sha);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getHostAddress().hashCode() * 127 + this.sha.hashCode();
	}

	@Override
	public String toString() {
		return String.format("{IP: %s, Sha: %s}", this.getHostAddress(), this.getSha().toString().substring(0, 5));
	}
}
