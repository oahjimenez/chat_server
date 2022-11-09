package chatroom;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class MainServer {

	public static int COM_PORT = 1009;

	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(COM_PORT);
			new ChatServerGUI("rmi://localhost:" + COM_PORT + "/GroupChatService");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
