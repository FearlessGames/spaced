package se.spaced.server;

public interface ServerNotifier {
	void notifyServerCrash(Exception e);
}
