import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

import dht_client.Client;
import dht_node.DistributedHashTable;
import util.Lib;
import util.Logger;
import util.NodeInfo;
import util.Sha256;

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
			launchClientNode(Arrays.copyOfRange(args, 1, args.length));
			break;
		default:
			throw new RuntimeException("Not recognized option: " + args[0]);
		}
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
		while (true)
			;
	}

	private static void launchClientNode(String... workNodeIPs) {
		Client client = new Client();
		for (String s : workNodeIPs) {
			client.addWokerNodeIP(s);
		}
		Scanner in = new Scanner(System.in);
		while (in.hasNext()) {
			String input = in.nextLine();
			String[] sp = input.split("\\s+");
			switch (sp[0]) {
			case "get":
				client.get(sp[1], (inputStream) -> {
					if (inputStream != null)
						try {
							Lib.copyLarge(inputStream, System.out);
						} catch (IOException e) {
							e.printStackTrace();
						}
					System.out.println();
				});
				break;
			case "put":
				client.put(sp[1], (outputStream)->{
					if (outputStream != null) {
						try {
							outputStream.write(sp[2].getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				break;
			default:
				Logger.logError("Not recognized command: %s", sp[0]);
				break;
			}
		}
		in.close();
	}
}
