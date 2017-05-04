package distributedHashTable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {
		String hostNameToJoin = args[0];
		System.out.println(hostNameToJoin);
		
		DistributedHashTable dht = DistributedHashTable.getIntance();
		dht.joinCluster(InetAddress.getByName(hostNameToJoin));

		while (true)
			;
	}

}
