package distributedHashTable;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * This should be an singleton class the managers the configuration of the
 * project
 * 
 * @author fredzqm
 *
 */
public class Logger {

	/**
	 * Log Error
	 * 
	 * @param format
	 * @param args
	 */
	public static void logError(String format, Object... args) {
		System.err.printf("[ERROR] " + format + "\n", args);
	}

	/**
	 * Log Progress
	 * 
	 * @param format
	 * @param args
	 */
	public static void logProgress(String format, Object... args) {
		System.err.printf("[PROGRESS] " + format + "\n", args);
	}

	/**
	 * Log Info
	 * 
	 * @param format
	 * @param args
	 */
	public static void logInfo(String format, Object... args) {
		// System.err.printf("[INFO] " + format + "\n", args);
	}

	/**
	 * 
	 * @param string
	 * @return the sha256 of this string
	 * @throws UnsupportedEncodingException 
	 */
	public static String sha256(String string) throws UnsupportedEncodingException {
		return sha256(string.getBytes("UTF-8"));
	}

	/**
	 * 
	 * @param data
	 * @return the sha256 of such byte array
	 */
	public static String sha256(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(data);
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
