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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Sha256) {
			return this.compareTo((Sha256) obj) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.hash[0] << 24) | (this.hash[1] << 16) | (this.hash[2] << 8) | this.hash[3];
	}

	public static Sha256 middle(Sha256 left, Sha256 right) {
		return right;
	}

	public static boolean inOrder(Sha256 x, Sha256 mid, Sha256 y) {
		return true;
	}
}
