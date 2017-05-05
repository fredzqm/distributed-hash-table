package distributedHashTable;

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
		System.err.printf("[INFO] " + format + "\n", args);
	}

}
