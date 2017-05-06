
import java.net.InetAddress;
import java.net.UnknownHostException;

import distributedHashTable.DistributedHashTable;
import distributedHashTable.Logger;
import distributedHashTable.Sha256;

public class Main {

	public static void main(String[] args) throws UnknownHostException {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		Logger.logProgress("The IP of this node is %s", InetAddress.getLocalHost().getHostAddress());
		if (args.length > 0) {
			String hostNameToJoin = args[0];
			Logger.logProgress("Attempting to join cluster from entry host: %s", hostNameToJoin);
			dht.joinCluster(InetAddress.getByName(hostNameToJoin));
		} else {
			Logger.logProgress(
					"No argument passed in, skip attempting to join another host, initializing a random sha");
			dht.setMySha(new Sha256("" + Math.random()));
		}
		while (true)
			;
	}

}
