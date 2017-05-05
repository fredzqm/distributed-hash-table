package distributedHashTable;

import java.io.Serializable;
import java.math.BigInteger;
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
	private transient BigInteger num;

	/**
	 * 
	 * @param string
	 */
	public Sha256(String string) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			hash = digest.digest(string.getBytes());
			assert hash.length == 32;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public BigInteger getNum() {
		if (num == null) {
			num = new BigInteger(1, hash);
		}
		return num;
	}

	/**
	 * Constructs an Sha256 gives the sha byte array
	 * 
	 * @param the
	 *            hash
	 */
	public Sha256(byte[] sha) {
		this.hash = sha;
	}

	@Override
	public int compareTo(Sha256 o) {
		return getNum().compareTo(o.getNum());
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
			return this.getNum().equals(((Sha256) obj).getNum());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.hash[0] << 24) | (this.hash[1] << 16) | (this.hash[2] << 8) | this.hash[3];
	}

	public static Sha256 middle(Sha256 left, Sha256 right) {
		BigInteger leftNum = left.getNum();
		BigInteger rightNum = right.getNum();
		BigInteger ret;
		if (leftNum.compareTo(rightNum) < 0) {
			ret = leftNum.add(rightNum).shiftRight(1);
		} else {
			ret = leftNum.add(rightNum).add(getMaxPosSha()).shiftRight(1);
			if (ret.compareTo(getMaxPosSha()) >= 0) {
				ret = ret.subtract(getMaxPosSha());
			}
		}
		return new Sha256(ret.toByteArray());
	}

	private static BigInteger maxPosShaNum;

	protected static BigInteger getMaxPosSha() {
		if (maxPosShaNum == null) {
			byte[] x = new byte[33];
			x[0] = 1;
			maxPosShaNum = new BigInteger(x);
		}
		return maxPosShaNum;
	}

	public static boolean inOrder(Sha256 x, Sha256 mid, Sha256 y) {
		return x.compareTo(mid) * mid.compareTo(y) * x.compareTo(y) <= 0;
	}
}
