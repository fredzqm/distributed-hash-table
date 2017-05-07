package dht_node;

import java.net.InetAddress;

import request.Message;

/**
 * Search through dht on file
 * @author yangr
 */


public class GetRequest extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GetRequest(int ackForID) {
		super(ackForID);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getTimeOut() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void timeOut(InetAddress address) {
		// TODO Auto-generated method stub

	}

}
