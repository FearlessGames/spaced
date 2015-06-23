package se.spaced.client.net.ping;

public interface PingManager {
	void pong(long receivedTimestamp, int pingId);

	long getLatency();

	void sendPingRequest();
}
