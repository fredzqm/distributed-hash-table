package distributedHashTable;

/**
 * This should be an singleton class the managers the configuration of the
 * project
 * 
 * @author fredzqm
 *
 */
public class Settings {

	/**
	 * 
	 * @return true if [ERROR] logs will be shown
	 */
	public static boolean isError() {
		return true;
	}

	/**
	 * 
	 * @return true if [PROGRESS] logs will be shown
	 */
	public static boolean isProgress() {
		return true;
	}
	
	/**
	 * 
	 * @return true if [INFO] logs will be shown
	 */
	public static boolean isInfo() {
		return false;
	}
}
