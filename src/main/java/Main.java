
public class Main {

	public static void main(String[] args) {
		String hostNameToJoin = args[0];				
		DistributedHashTable dht = DistributedHashTable.getIntance();
		dht.joinCluster(hostNameToJoin);
	}

}
