import java.net.InetAddress;
import java.net.UnknownHostException;

import dht_node.DistributedHashTable;
import dht_node.Logger;
import dht_node.NodeInfo;
import dht_node.Sha256;

/**
 * The entry of the program responsible for parsing arguments
 * 
 * @author fredzqm
 *
 */
public class Main {
	public static void main(String[] args) throws UnknownHostException {
		Logger.logProgress("The IP of this node is %s", InetAddress.getLocalHost().getHostAddress());
		switch (args[0]) {
		case "node":
			launchWorkerNode(args.length >= 2 ? args[1] : null);
			break;
		case "client":
			launchClientNode(args[1]);
			break;
		default:
			throw new RuntimeException("Not recognized option: " + args[0]);
		}
		while (true)
			;
	}

	private static void launchWorkerNode(String hostNameToJoin) throws UnknownHostException {
		DistributedHashTable dht = DistributedHashTable.getIntance();
		if (hostNameToJoin != null) {
			Logger.logProgress("Attempting to join cluster from entry host: %s", hostNameToJoin);
			dht.joinCluster(InetAddress.getByName(hostNameToJoin));
		} else {
			Logger.logProgress(
					"No argument passed in, skip attempting to join another host, initializing a random sha");
			dht.setMySelf(new NodeInfo(InetAddress.getLocalHost(), new Sha256("" + Math.random())));
		}
	}

	private static void launchClientNode(String string) {
		// TODO Auto-generated method stub
		
	}
}
