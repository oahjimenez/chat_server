package chatroom.monitor;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import chatroom.domain.ConnectedClient;

public class MessageSenderMonitor {
	
	public synchronized void sendForChannelToAll(String newMessage, String channel, Vector<ConnectedClient> clients) {
		for (ConnectedClient c : clients) {
			try {
				c.getClient().messageFromServerToChannel(newMessage, channel);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
