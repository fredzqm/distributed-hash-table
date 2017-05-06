package dht_node;

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

}
