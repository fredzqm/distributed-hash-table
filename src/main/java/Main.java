
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import distributedHashTable.DistributedHashTable;
import distributedHashTable.Settings;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException, SocketTimeoutException {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		if (args.length > 0) {
			String hostNameToJoin = args[0];
			if (Settings.isProgress())
				System.out.println("[PROGRESS] Attempting to join cluster from entry host: " + hostNameToJoin);
			dht.joinCluster(InetAddress.getByName(hostNameToJoin));
		} else {
			if (Settings.isProgress())
				System.out.println("[PROGRESS] No argument passed in, skip attempting to join another host");
		}
		while (true)
			;
	}

}
