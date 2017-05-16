package networkUtility;

import java.util.PriorityQueue;

public class Timer implements Runnable {
	private static Timer instance;
	private PriorityQueue<TimeOutEvent> unACKedMessages = new PriorityQueue<>();

	public static void setTimeOut(long time, Runnable callback) {
		Timer.getInstance()._setTimeOut(time, callback);
	}
	
	private synchronized void _setTimeOut(long time, Runnable callback) {
		unACKedMessages.add(new TimeOutEvent(time, callback));
		notifyAll();
	}

	@Override
	public synchronized void run() {
		while (true) {
			while (this.unACKedMessages.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long left = this.unACKedMessages.peek().getTime() - System.currentTimeMillis();
			if (left > 0) {
				try {
					this.wait(left);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (!this.unACKedMessages.isEmpty()) {
				TimeOutEvent next = this.unACKedMessages.peek();
				if (!next.checkTimeOut())
					break;
				this.unACKedMessages.poll();
			}
		}
	}

	private static class TimeOutEvent implements Comparable<TimeOutEvent> {
		private long time;
		private Runnable callback;

		public TimeOutEvent(long timeOut, Runnable callback) {
			this.time = System.currentTimeMillis() + timeOut;
			this.callback = callback;
		}

		/**
		 * Check if this event has timed out
		 * @return true if this event has timedOut and callback has been called
		 */
		public boolean checkTimeOut() {
			if (this.time < System.currentTimeMillis()) {
				callback.run();
				return true;
			}
			return false;
		}

		public long getTime() {
			return time;
		}

		@Override
		public int compareTo(TimeOutEvent o) {
			return (int) (time - o.time);
		}

	}

	public static Timer getInstance() {
		if (instance == null) {
			synchronized (Timer.class) {
				if (instance == null) {
					instance = new Timer();
					new Thread(instance).start();
				}
			}
		}
		return instance;
	}

}
