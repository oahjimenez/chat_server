package src;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Date;
import java.util.Vector;

public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {
	String line = "---------------------------------------------\n";
	private Vector<ConnectedClient> clients;
	private static final long serialVersionUID = 1L;

	public ChatServer() throws RemoteException {
		super();
		clients = new Vector<ConnectedClient>(10, 1);
	}

	public String sayHello(String ClientName) throws RemoteException {
		System.out.println(ClientName + " sent a message");
		return "Hello " + ClientName + " from group chat server";
	}

	@Override
	public void updateChat(String name, String nextPost) throws RemoteException {
		String message = name + " : " + nextPost + "\n";
		sendToAll(message);
	}

	@Override
	public void passIDentity(RemoteRef ref) throws RemoteException {
		try {
			// System.out.println("\n" + ref.remoteToString() + "\n");
			System.out.println(line + ref.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receive a new client and display details to the console send on to register
	 * method register the clients interface and store it in a reference for future
	 * messages to be sent to, ie other members messages of the chat session. send a
	 * test message for confirmation / test connection
	 */
	@Override
	public void registerListener(String[] details) throws RemoteException {
		System.out.println(new Date(System.currentTimeMillis()));
		System.out.println(details[0] + " has joined the chat session");
		System.out.println(details[0] + "'s hostname : " + details[1]);
		System.out.println(details[0] + "'s port : " + details[2]);
		System.out.println(details[0] + "'s RMI service : " + details[3]);
		try {
			ChatClientInterface nextClient = (ChatClientInterface) Naming
					.lookup("rmi://" + details[1] + ":" + details[2] + "/" + details[3]);

			clients.addElement(new ConnectedClient(details[0], nextClient));

			nextClient.messageFromServer("[Server] : Hello " + details[0] + " you are now free to chat.\n");

			sendToAll("[Server] : " + details[0] + " has joined the group.\n");

			updateUserList();
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update all clients by remotely invoking their updateUserList RMI method
	 */
	private void updateUserList() {
		String[] currentUsers = getUserList();
		for (ConnectedClient c : clients) {
			try {
				c.getClient().updateUserList(currentUsers);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private String[] getUserList() {
		String[] allUsers = new String[clients.size()];
		for (int i = 0; i < allUsers.length; i++) {
			allUsers[i] = clients.elementAt(i).getName();
		}
		return allUsers;
	}

	private void sendToAll(String newMessage) {
		for (ConnectedClient c : clients) {
			try {
				c.getClient().messageFromServer(newMessage);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * remove a client from the list, notify everyone
	 */
	@Override
	public void leaveChat(String userName) throws RemoteException {

		for (ConnectedClient c : clients) {
			if (c.getName().equals(userName)) {
				System.out.println(line + userName + " left the chat session");
				System.out.println(new Date(System.currentTimeMillis()));
				clients.remove(c);
				break;
			}
		}
		if (!clients.isEmpty()) {
			updateUserList();
		}
	}

	/**
	 * A method to send a private message to selected clients The integer array
	 * holds the indexes (from the clients vector) of the clients to send the
	 * message to
	 */
	@Override
	public void sendPM(int[] privateGroup, String privateMessage) throws RemoteException {
		ConnectedClient pc;
		for (int i : privateGroup) {
			pc = clients.elementAt(i);
			pc.getClient().messageFromServer(privateMessage);
		}
	}
}