package src;

import java.rmi.*;
import java.rmi.server.RemoteRef;
import java.util.List;

public interface ChatServerInterface extends Remote {

	public void updateChat(String userName, String chatMessage, String channelName) throws RemoteException;

	public void passIDentity(RemoteRef ref) throws RemoteException;

	public void registerListener(String[] details) throws RemoteException;

	public void leaveChat(String userName, String channelName) throws RemoteException;

	public void sendPM(int[] privateGroup, String privateMessage) throws RemoteException;
	
	public List<String> getChannelsName() throws RemoteException;
	public void goToChannel(String userName,String newChannelName,String oldChannelName) throws RemoteException;
}
