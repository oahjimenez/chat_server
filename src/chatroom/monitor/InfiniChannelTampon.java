package chatroom.monitor;

/**
 * Producer-Consumer Monitor
 */
public class InfiniChannelTampon {
	private volatile int val = 0;
	private volatile boolean isWriting;
	private volatile boolean isReading;
	private boolean tamponClosed;
	
	public static final String TAMPON_CLOSED_EXCEPTION = "The buffer has been closed";
	
	public synchronized void prod(int number) throws Exception {
		while ( isWriting || isReading ) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isWriting=true;
		
		if (number==val+1) {
			val = number;
			isWriting=false;
			notifyAll();
		}else {
			isWriting=false;
			notifyAll();
			throw new Exception("La valeur envoye est incorrecte!");
		}
	}

	public synchronized int cons() throws IllegalStateException {
		while (isWriting) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (tamponClosed) {
			throw new IllegalStateException(TAMPON_CLOSED_EXCEPTION);
		}
		
		isReading=true;
		int tmp = val;
		isReading=false;
		notifyAll();
		return tmp;
	}
	
	synchronized void terminate() {
		tamponClosed = true;
		notifyAll();
	}			
}
