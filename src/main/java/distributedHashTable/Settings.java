package distributedHashTable;

/**
 * This should be an singleton class the managers the configuration of the
 * project
 * 
 * @author fredzqm
 *
 */
public class Settings {
	private final static boolean VERBOSE = true;

	/**
	 * 
	 * @return true if [INFO} logs will be shown
	 */
	public static boolean isVerbose() {
		return VERBOSE;
	}

}
