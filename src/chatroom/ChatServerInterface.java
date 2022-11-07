package chatroom;

import java.rmi.*;
import java.rmi.server.RemoteRef;
import java.util.List;

public interface ChatServerInterface extends Remote {

	public void updateChat(String userName, String chatMessage, String channelName) throws RemoteException;

	public void passIDentity(RemoteRef ref) throws RemoteException;

	public void registerListener(String[] details) throws RemoteException,Exception;

	public void leaveChat(String userName) throws RemoteException;

	public void sendPM(int[] privateGroup, String privateMessage) throws RemoteException;
	public void sendException(String userName, String exception) throws RemoteException;
	
	public List<String> getChannelsName() throws RemoteException;
	public void goToChannel(String userName,String channelName) throws RemoteException;
	public int getLastInfiniValue() throws RemoteException;
}
