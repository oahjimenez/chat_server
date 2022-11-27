package chatroom;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Logger;

public class MainServer {

	public static final int COM_PORT = 1009;
	public static final String HOST = "localhost";
	public static final String GROUP_CHAT_SERVICE = "GroupChatService";

	private static final Logger log = Logger.getLogger(MainServer.class.getName());

	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(COM_PORT);
			new ChatServerGUI(String.format("rmi://%s:%s/%s", HOST, COM_PORT, GROUP_CHAT_SERVICE));
		} catch (RemoteException e) {
			log.severe(e.getMessage());
		}
	}

}
