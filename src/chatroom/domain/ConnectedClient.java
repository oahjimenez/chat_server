package chatroom.domain;

import java.util.Objects;

import chatroom.ChatClientInterface;

/**
 * Connected client POJO
 */
public class ConnectedClient {
	public String name;
	public ChatClientInterface client;

	public ConnectedClient(String name, ChatClientInterface client) {
		this.name = name;
		this.client = client;
	}
	
	public ConnectedClient(ConnectedClient c) {
		this.name = c.getName();
		this.client = c.getClient();
	}

	public String getName() {
		return name;
	}

	public ChatClientInterface getClient() {
		return client;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectedClient other = (ConnectedClient) obj;
		return Objects.equals(name, other.name);
	}
}
