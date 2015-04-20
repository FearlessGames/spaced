package se.fearless.bender.commands;

import org.simpleframework.http.Request;
import org.simpleframework.transport.connect.SocketConnection;
import se.fearless.bender.IrcAnnouncer;

import java.io.IOException;

public class ShutdownCommand implements Command {
	private final IrcAnnouncer ircAnnouncer;
	private final SocketConnection connection;

	public ShutdownCommand(IrcAnnouncer ircAnnouncer, SocketConnection connection) {
		this.ircAnnouncer = ircAnnouncer;
		this.connection = connection;
	}

	@Override
	public void execute(Request type) {
		ircAnnouncer.quitServer("Well, I'm rich. Goodbye, losers whom I always hated!");
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
