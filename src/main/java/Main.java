
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import distributedHashTable.DistributedHashTable;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException, SocketTimeoutException {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		if (args.length > 0) {
			String hostNameToJoin = args[0];
			System.out.println(hostNameToJoin);
			System.out.println("[INTO] Attempting to join cluster from entry host: " + hostNameToJoin);
			dht.joinCluster(InetAddress.getByName(hostNameToJoin));
		} else {
			System.out.println("[INTO] No argument passed in, skip attempting to join another host");
		}
		while (true)
			;
	}

}
