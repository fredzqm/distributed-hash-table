package distributedHashTable;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 implements Serializable, Comparable<Sha256> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The 32 bytes, 256 bits hash
	 */
	private final byte[] hash;

	/**
	 * 
	 * @param string
	 * @return the sha256 of this string
	 * @throws UnsupportedEncodingException
	 */
	public Sha256(String string) {
		this(string.getBytes());
	}

	/**
	 * 
	 * @param data
	 * @return the sha256 of such byte array
	 */
	public Sha256(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			hash = digest.digest(data);
			assert hash.length == 32;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int compareTo(Sha256 o) {
		for (int i = 0; i < 32; i++) {
			int diff = this.hash[i] - o.hash[i];
			if (diff != 0)
				return diff;
		}
		Logger.logError("There is an Sha256 collision");
		return 0;
	}

	@Override
	public String toString() {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < 32; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}
}
