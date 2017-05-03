import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {
		String hostNameToJoin = args[0];
		// DistributedHashTable dht = DistributedHashTable.getIntance();
		// dht.joinCluster(hostNameToJoin);
		RequestParser s = new RequestParser();
		Server.sendObject(new HelloRequest(), InetAddress.getByName(hostNameToJoin), RequestParser.PORT);
		while (true)
			;
	}

}
