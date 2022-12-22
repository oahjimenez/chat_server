package chatroom.monitor;

/**
 * Producer-Consumer Monitor
 */
public class SpeakUpChannelTampon {
	
	private volatile String username;
	public static final String TAMPON_CLOSED_EXCEPTION = "The buffer has been closed";
	
	public synchronized void redacteur(String user) {
		if (username!=null ) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		username=user;
		notify();
	}
	
	public synchronized void stopSpeakUp() {
		this.username=null;
		notify();
	}

	public synchronized String lecteur() {
		return username;
	}
}
