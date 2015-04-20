package se.fearless.bender.commands;

import org.jibble.pircbot.Colors;
import org.simpleframework.http.Request;
import se.fearless.bender.Commit;
import se.fearless.bender.CommitFactory;
import se.fearless.bender.IrcAnnouncer;

import java.io.IOException;

public class GithubCommand implements Command {
	private final IrcAnnouncer ircAnnouncer;

	public GithubCommand(final IrcAnnouncer ircAnnouncer) {
		this.ircAnnouncer = ircAnnouncer;
	}

	@Override
	public void execute(final Request request) {
		try {
			Commit commit = CommitFactory.create(request);
			announceCommit(commit);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void announceCommit(Commit commit) {
		ircAnnouncer.announce(Colors.GREEN + "Commit " + commit);
	}

}
