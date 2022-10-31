package src;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class MainServer {

	public static int COM_PORT = 1009;

	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(COM_PORT);
			System.out.println("RMI Server ready");
			Naming.rebind("rmi://localhost:" + COM_PORT + "/GroupChatService", new ChatServer());
			System.out.println("Group Chat RMI Server is running...");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
