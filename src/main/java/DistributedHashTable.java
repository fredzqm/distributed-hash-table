import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DistributedHashTable {
	private static DistributedHashTable table;
	private Set<String> addresses;
	private Map<String, String> map;

	private DistributedHashTable() {
		this.addresses = new HashSet<>();
		this.map = new HashMap<>();
	}

	public String get(String fileName) {
		if (map.containsKey(fileName)) {
			return map.get(fileName);
		}
		return null;
	}

	public String put(String fileName, String content) {
		// Check if the file is in the right address
		if (map.containsKey(fileName)) {
			return "File already exists!";
		}
		map.put(fileName, content);
		return "File successfully added!";
	}

	public boolean remove(String fileName) {
		if (map.containsKey(fileName)) {
			map.remove(fileName);
			return true;
		}
		return deleteInOtherAddresses(fileName);
	}

	public boolean deleteInOtherAddresses(String filename) {
		return false;
	}

	public String searchInOtherAddresses(String filename) {
		return null;
	}

	public DistributedHashTable getIntance() {
		if (table == null) {
			synchronized (this) {
				if (table == null) {
					table = new DistributedHashTable();
				}
			}
		}
		return table;
	}
}
