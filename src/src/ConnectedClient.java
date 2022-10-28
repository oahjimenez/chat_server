package src;

public class ConnectedClient {
	public String name;
	public ChatClientInterface client;

	public ConnectedClient(String name, ChatClientInterface client) {
		this.name = name;
		this.client = client;
	}

	public String getName() {
		return name;
	}

	public ChatClientInterface getClient() {
		return client;
	}
}
