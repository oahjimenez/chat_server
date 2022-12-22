package chatroom;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.util.List;

/**
 * Interface allowing the remote invocation of server methods
 */
public interface ChatServerInterface extends Remote {

	public void passIDentity(RemoteRef ref) throws RemoteException;

	public boolean registerListener(String[] details) throws RemoteException;
	
	public boolean isUsernameAvailable(String username) throws RemoteException;


	public void updateChat(String userName, String chatMessage, String channelName) throws RemoteException;

	public void sendPM(int[] privateGroup, String privateMessage) throws RemoteException;

	public void sendException(String userName, String exception) throws RemoteException;

	public void leaveChat(String userName) throws RemoteException;
	

	public List<String> getChannelsName() throws RemoteException;

	public void goToChannel(String userName, String channelName) throws RemoteException;
	
	public void subscribeToChannels(String userName, List<String> channelNames) throws RemoteException;


	public int getLastInfiniValue() throws RemoteException;

	public String getSpeakerUsername() throws RemoteException;

	public void speakUp(String username) throws RemoteException;

	public void stopSpeakUp() throws RemoteException;

}
