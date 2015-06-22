package se.spaced.server.net;

import java.io.IOException;

public interface RemoteServer {
	void setTimeout(int timeout);

	void startup() throws IOException;

	void shutdown();

	boolean isRunning();
}
