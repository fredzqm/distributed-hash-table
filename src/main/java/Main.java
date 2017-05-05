
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import distributedHashTable.DistributedHashTable;
import distributedHashTable.Logger;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException, SocketTimeoutException {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		if (args.length > 0) {
			String hostNameToJoin = args[0];
			Logger.logProgress("Attempting to join cluster from entry host: %s", hostNameToJoin);
			dht.joinCluster(InetAddress.getByName(hostNameToJoin));
		} else {
			Logger.logProgress("No argument passed in, skip attempting to join another host");
		}
		while (true)
			;
	}

}
