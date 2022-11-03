package chatroom;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import chatroom.domain.ConnectedClient;

public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {
	String line = "---------------------------------------------\n";
	private Map<String,Vector<ConnectedClient>> channelClients;
	private Vector<ConnectedClient> allClients;
	private static final long serialVersionUID = 1L;

	public ChatServer() throws RemoteException {
		super();
		allClients = new Vector<ConnectedClient>(10, 1);
		channelClients = new LinkedHashMap<String,Vector<ConnectedClient>>();
		channelClients.put("#general", new Vector<ConnectedClient>(10, 1));
		channelClients.put("#off-topic", new Vector<ConnectedClient>(10, 1));
		channelClients.put("#middleware", new Vector<ConnectedClient>(10, 1));
	}


	@Override
	public void updateChat(String name, String nextPost, String channelName) throws RemoteException {
		String message = name + " : " + nextPost + "\n";
		sendToAll(message, channelClients.get(channelName));
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

			allClients.addElement(new ConnectedClient(details[0], nextClient));
			channelClients.get("#general").addElement(new ConnectedClient(details[0], nextClient));

			nextClient.messageFromServer("[Server] : Hello " + details[0] + " you are now free to chat.\n");

			sendToAll("[Server] : " + details[0] + " has joined the chat group.\n",allClients);

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
		for (ConnectedClient c : allClients) {
			try {
				c.getClient().updateUserList(currentUsers);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private String[] getUserList() {
		String[] allUsers = new String[allClients.size()];
		for (int i = 0; i < allUsers.length; i++) {
			allUsers[i] = allClients.elementAt(i).getName();
		}
		return allUsers;
	}

	private void sendToAll(String newMessage, Vector<ConnectedClient> clients) {
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
	public void leaveChat(String userName, String channelName) throws RemoteException {
		Vector<ConnectedClient> clients = channelClients.get(channelName);
		if(clients!=null) {			
			for (ConnectedClient c : clients) {
				if (c.getName().equals(userName)) {
					System.out.println(line + userName + " left the chat session");
					System.out.println(new Date(System.currentTimeMillis()));
					clients.remove(c);
					allClients.remove(c);
					break;
				}
			}
		}		


		if (!allClients.isEmpty()) {
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
			pc = allClients.elementAt(i);
			pc.getClient().messageFromServer(privateMessage);
		}
	}

	@Override
	public List<String> getChannelsName() throws RemoteException {
		return new ArrayList<String>(channelClients.keySet());
	}

	@Override
	public void goToChannel(String userName,String newChannelName,String oldChannelName) throws RemoteException {
		if (channelClients.containsKey(oldChannelName) ) {
			channelClients.get(oldChannelName).removeIf( c -> c.getName().equals(userName));
		}
		
		if (channelClients.containsKey(newChannelName) ) {
			ConnectedClient copy = null;
			for (ConnectedClient c : allClients) {
				if (c.getName().equals(userName)) {
					copy = new ConnectedClient(c);
					break;
				}
			}
			channelClients.get(newChannelName).add(copy) ;
		}
	}
	
	
}