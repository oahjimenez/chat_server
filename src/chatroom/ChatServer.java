package chatroom;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import chatroom.domain.ConnectedClient;
import chatroom.monitor.InfiniChannelTampon;
import chatroom.monitor.SpeakUpChannelTampon;

public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {
	
	private static final long serialVersionUID = -727355867136017036L;
	
	public static final String LINE = "---------------------------------------------\n";
	public static final DateTimeFormatter FULL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");

	private static final Logger log = Logger.getLogger(ChatServer.class.getName());
	
	private Map<String,Vector<ConnectedClient>> channelClients;
	private Vector<ConnectedClient> allClients;
	private InfiniChannelTampon infiniChannelTampon;
	private SpeakUpChannelTampon speakUpChannelTampon;

	public ChatServer() throws RemoteException {
		super();
		allClients = new Vector<ConnectedClient>(10, 1);
		channelClients = new LinkedHashMap<String,Vector<ConnectedClient>>();
		channelClients.put("#general", new Vector<ConnectedClient>(10, 1));
		channelClients.put("#off-topic", new Vector<ConnectedClient>(10, 1));
		channelClients.put("#middleware", new Vector<ConnectedClient>(10, 1));
		channelClients.put("#infini", new Vector<ConnectedClient>(10, 1));
		channelClients.put("#speak-up", new Vector<ConnectedClient>(10, 1));
		infiniChannelTampon = new InfiniChannelTampon();
		speakUpChannelTampon = new SpeakUpChannelTampon();
	}


	@Override
	public void updateChat(String name, String nextPost, String channelName) throws RemoteException {
		String message = String.format("[%s] %s : %s\n",LocalDateTime.now().format(FULL_DATE_FORMATTER),name,nextPost);
		if (channelName.equals("#infini")) {
			try {
				int val = Integer.parseInt(nextPost);
				infiniChannelTampon.prod(val);
				sendForChannelToAll(message, channelName ,channelClients.get(channelName));
			} catch (NumberFormatException e) {
				sendException(name, "Invalid fomat, you need to send a numeric value!");
			} catch (Exception e) {
				sendException(name, e.getMessage());
			}

		}else {		
			sendForChannelToAll(message, channelName, channelClients.get(channelName));	
		}
	}

	@Override
	public void passIDentity(RemoteRef ref) throws RemoteException {
		try {
			// System.out.println("\n" + ref.remoteToString() + "\n");
			System.out.println(LINE + ref.toString());
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
	public void registerListener(String[] details) throws RemoteException,Exception {
		if (allClients.stream().anyMatch(t -> t.getName().toLowerCase().equals(details[0].toLowerCase()))) {
			throw new Exception("This username is already used!");
		}else {
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
	
				//nextClient.messageFromServerToChannel("[Server] : Hello " + details[0] + " you are now free to chat.\n","#general");
	
				sendForChannelToAll("[Server] : " + details[0] + " has joined the chat group.\n", "#general",allClients);
	
				updateUsersListForAllClients();
			} catch (RemoteException | MalformedURLException | NotBoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Update all clients by remotely invoking their updateUserList RMI method
	 */
	private void updateUsersListForAllClients() {
		String[] currentUsers = new String[allClients.size()];
		for (int i = 0; i < currentUsers.length; i++) {
			currentUsers[i] = allClients.elementAt(i).getName();
		}
		for (ConnectedClient c : allClients) {
			try {
				c.getClient().updateUserList(currentUsers);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendForChannelToAll(String newMessage, String channel, Vector<ConnectedClient> clients) {
		for (ConnectedClient c : clients) {
			try {
				c.getClient().messageFromServerToChannel(newMessage,channel);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendCloseToAllChannel(String newMessage) {
		for (Map.Entry<String,Vector<ConnectedClient>>  set :channelClients.entrySet()) {
			for (ConnectedClient c : set.getValue()) {
				try {
					c.getClient().messageFromServerToChannel(newMessage,set.getKey());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
	    }
		for (ConnectedClient c : allClients) {
			try {
				c.getClient().serverIsClosing();
			} catch (RemoteException e) {
				log.severe(e.getMessage());
			}
		}
	}

	/**
	 * remove a client from the list, notify everyone
	 */
	@Override
	public void leaveChat(String userName) throws RemoteException {
		for (Map.Entry<String,Vector<ConnectedClient>>  set :channelClients.entrySet()) {			
			if(set.getValue().removeIf(c -> c.getName().equals(userName) )) {
				System.out.println(LINE + userName + " left the chat session");
				System.out.println(new Date(System.currentTimeMillis()));
			}
	    }
		allClients.removeIf(c -> c.getName().equals(userName) );				

		if (!allClients.isEmpty()) {
			updateUsersListForAllClients();
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
			pc.getClient().messageFromServerToChannel(privateMessage,"#pm");
		}
	}
	
	/**
	 * A method to send a exception to a client
	 */
	@Override
	public void sendException(String userName, String privateMessage) throws RemoteException {
		for (ConnectedClient c : allClients) {
			if (c.getName().equals(userName)) {
				c.getClient().exceptionFromServer(privateMessage);
				break;
			}
		}
	}

	@Override
	public List<String> getChannelsName() throws RemoteException {
		return new ArrayList<String>(channelClients.keySet());
	}

	@Override
	public void goToChannel(String userName,String channelName) throws RemoteException {
		if (channelClients.containsKey(channelName) && 
				!channelClients.get(channelName).stream().anyMatch(c -> c.getName().equals(userName))) {	
				Optional<ConnectedClient> result = allClients.stream().filter(c -> c.getName().equals(userName)).findFirst();
				result.ifPresent(c -> channelClients.get(channelName).add( new ConnectedClient(c) ) );
		}
	}
	
	@Override
	public void subscribeToChannels(String userName,List<String> channelNames) throws RemoteException {
		Optional<ConnectedClient> client = allClients.stream().filter(c -> c.getName().equals(userName)).findFirst();
		if (!client.isPresent()) {
			log.info(String.format("%s not logged in", userName));
			return;
		}
		List<String> susbscribableChannels = channelNames.stream().filter(channel -> channelClients.containsKey(channel)).collect(Collectors.toList());
		for (String channelName: susbscribableChannels) {
			if (!channelClients.get(channelName).contains(client.get())) {
				channelClients.get(channelName).add( new ConnectedClient(client.get()));
			};
		}
	}
	
	@Override
	public int getLastInfiniValue() throws RemoteException{
		return infiniChannelTampon.cons();
	}
	
	@Override
	public String getSpeakerUsername() throws RemoteException{
		return speakUpChannelTampon.lecteur();
	}

	@Override
	public void speakUp(String username) throws RemoteException{
		speakUpChannelTampon.redacteur(username);
	}
	
	@Override
	public void stopSpeakUp() throws RemoteException{
		speakUpChannelTampon.stopSpeakUp();
	}
}