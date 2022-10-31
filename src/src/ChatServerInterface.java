package src;

import java.rmi.*;
import java.rmi.server.RemoteRef;

public interface ChatServerInterface extends Remote {

	public void updateChat(String userName, String chatMessage) throws RemoteException;

	public void passIDentity(RemoteRef ref) throws RemoteException;

	public void registerListener(String[] details) throws RemoteException;

	public void leaveChat(String userName) throws RemoteException;

	public void sendPM(int[] privateGroup, String privateMessage) throws RemoteException;
}
