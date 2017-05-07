package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	 * @return the message printed
	 */
	public static String logError(String format, Object... args) {
		String mes = getString("ERROR", format, args);
		System.err.println(mes);
		return mes;
	}

	/**
	 * Log Progress
	 * 
	 * @param format
	 * @param args
	 * @return the message printed
	 */
	public static String logProgress(String format, Object... args) {
		String mes = getString("PROGRESS", format, args);
		System.err.println(mes);
		return mes;
	}

	/**
	 * Log Info
	 * 
	 * @param format
	 * @param args
	 * @return the message printed
	 */
	public static String logInfo(String format, Object... args) {
		String mes = getString("INFO", format, args);
		System.err.println(mes);
		return mes;
	}

	private static String getString(String head, String format, Object... args) {
		return String.format("[" + head + "] " + format, args);
	}

	public static long copyLarge(final InputStream input, final OutputStream output) throws IOException {
		long count = 0;
		final byte[] buffer = new byte[1024 * 4];
		int n;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
