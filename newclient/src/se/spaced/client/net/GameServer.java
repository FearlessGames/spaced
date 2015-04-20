package se.spaced.client.net;

public class GameServer {
	private final String name;
	private final String url;

	public GameServer(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
