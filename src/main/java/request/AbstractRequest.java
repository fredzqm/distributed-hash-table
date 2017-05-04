package request;

public abstract class AbstractRequest implements Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int requstID;
	
	public void setRequestID(int requestID) {
		this.requstID = requestID;
	}

	public int getRequestID() {
		return requstID;
	}
	
	
	public abstract long getTimeOut();

	public abstract void timeOut();

}
