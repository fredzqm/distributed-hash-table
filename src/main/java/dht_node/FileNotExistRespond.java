package dht_node;

import java.io.File;
import java.net.InetAddress;

import request.Message;


/**
 * @author yangr
 *
 *	Respond to get dht file
 */
public class FileNotExistRespond extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileNotExistRespond(int ackForID) {
		super(ackForID);
		// TODO Auto-generated constructor stub
	}

	public FileNotExistRespond(File f) {
		super(0);
		// TODO Auto-generated constructor stub
	}

	public FileNotExistRespond() {
		super(0);
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
