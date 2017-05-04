import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {
		String hostNameToJoin = args[0];
		// DistributedHashTable dht = DistributedHashTable.getIntance();
		// dht.joinCluster(hostNameToJoin);
		System.out.println(hostNameToJoin);
		RequestParser s = new RequestParser();

		InetAddress addr = InetAddress.getByName(hostNameToJoin);
		UDPServer.sendObject(new HelloRequest(), addr, RequestParser.PORT);

		try {
			HelloResponse response = UDPServer.recieveObject(addr, 3333, 1000, HelloResponse.class);
			System.out.println("getResponse: " + response);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		}
		while (true)
			;
	}

}
