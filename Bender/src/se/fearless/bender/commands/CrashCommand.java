package se.fearless.bender.commands;

import org.jibble.pircbot.Colors;
import org.simpleframework.http.Request;
import se.fearless.bender.IrcAnnouncer;

import java.io.IOException;

public class CrashCommand implements Command {
	private final IrcAnnouncer ircAnnouncer;

	public CrashCommand(IrcAnnouncer ircAnnouncer) {
		this.ircAnnouncer = ircAnnouncer;
	}

	@Override
	public void execute(Request request) {
		try {
			String message = request.getParameter("message");
			ircAnnouncer.announce(Colors.YELLOW + "The server crashed: " + message);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

	}
}
