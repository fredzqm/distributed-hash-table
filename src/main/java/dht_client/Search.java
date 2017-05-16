package dht_client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import dht_node.DistributedHashTable;
import request.AbstractACKMessage;
import request.CommunicationHandler;
import request.Message;
import util.Logger;
import util.NodeInfo;
import util.Sha256;

public class Search extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Sha256 sha;
	private InetAddress clientAddress;

	private transient List<String> ipList;
	private transient int cur;
	private transient Callback callback;
	private boolean firstStop;

	public Search(Sha256 sha256, List<String> ips, Callback callback) {
		super(0);
		this.sha = sha256;
		this.callback = callback;
		try {
			this.clientAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		ipList = ips;
		cur = 0;
	}

	public void send() {
		firstStop = true;
		CommunicationHandler.sendMessage(this, ipList.get(cur));
	}

	@Override
	public void handleRequest(InetAddress address, Message acknowleged) {
		firstStop = false;
		DistributedHashTable dht = DistributedHashTable.getIntance();
		NodeInfo mySelf = dht.getMyself();
		NodeInfo right = dht.getRight();
		if (Sha256.inOrder(mySelf.getSha(), sha, right.getSha())) {
			CommunicationHandler.sendMessage(new Found(getRequestID()), clientAddress);
		} else {
			// TODO: optimize this, right now just pass to the right
			CommunicationHandler.sendMessage(this, right.getAddress());
		}
	}

	@Override
	public long getTimeOut() {
		return 10000;
	}

	@Override
	public void timeOut(InetAddress address) {
		int nextCur = (cur + 1) % ipList.size();
		Logger.logError("findNodeRequest for %s failed, start attempting %s", ipList.get(cur), ipList.get(nextCur));
		cur = nextCur;
		send();
	}

	@Override
	public boolean requireACK() {
		return firstStop;
	}

	public static class Found extends AbstractACKMessage {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Found(int ackForID) {
			super(ackForID);
		}

		@Override
		public void handleRequest(InetAddress address, Message acknowleged) {
			Search findRequest = (Search) acknowleged;
			Logger.logProgress("The source of %s is being found at %s", findRequest.sha, address.getHostAddress());
			new Thread(() -> {
				if (!findRequest.callback.onFoundNode(address))
					findRequest.send();
			}).start();
		}

	}

	/**
	 * 
	 * @author fredzqm
	 *
	 */
	public interface Callback {
		/**
		 * 
		 * @param address
		 *            the address of the node found that might has this file
		 * @return {@code true} if this connection is successfully consumed,
		 *         otherwise reattempt to find the node
		 */
		boolean onFoundNode(InetAddress address);
	}

}
