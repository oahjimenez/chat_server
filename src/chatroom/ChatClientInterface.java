package chatroom;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface allowing the remote invocation of client methods from server
 */
public interface ChatClientInterface extends Remote {

	public void messageFromServerToChannel(String message, String channel) throws RemoteException;

	public void exceptionFromServer(String message) throws RemoteException;

	public void updateUserList(String[] currentUsers) throws RemoteException;

	public void serverIsClosing() throws RemoteException;
}