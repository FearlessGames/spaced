package se.spaced.server.net.broadcast;

public interface SmrtBroadcaster<T> {

	SmrtBroadcastMessage<T> create();

	void addSpy(T spy);
}
