package se.spaced.client.net.smrt;

public interface ServerConnectionListener {
	void disconnected(String message);

	void connectionSucceeded(String host, int port);

	void connectionFailed(String errorMessage);
}
