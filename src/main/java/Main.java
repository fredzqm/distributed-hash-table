
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import distributedHashTable.DistributedHashTable;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException, SocketTimeoutException {
		if (args.length > 0) {
			String hostNameToJoin = args[0];
			System.out.println(hostNameToJoin);

			DistributedHashTable dht = DistributedHashTable.getIntance();
			dht.joinCluster(InetAddress.getByName(hostNameToJoin));
		}
		while (true)
			;
	}

}
