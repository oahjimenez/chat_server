package chatroom;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientInterface extends Remote {

	public void messageFromServerToChannel(String message, String channel) throws RemoteException;
	public void exceptionFromServer(String message) throws RemoteException;

	public void updateUserList(String[] currentUsers) throws RemoteException;

}