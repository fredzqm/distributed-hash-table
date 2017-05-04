import java.net.InetAddress;

public class HelloRequest implements Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
	public void handleRequest(InetAddress addr) {
		System.out.println("Hello world");
		UDPServer.sendObject(new HelloResponse(), addr, 3333);
	}

}
