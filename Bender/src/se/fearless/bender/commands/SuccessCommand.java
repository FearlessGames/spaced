package se.fearless.bender.commands;

import org.jibble.pircbot.Colors;
import org.simpleframework.http.Request;
import se.fearless.bender.BuildState;
import se.fearless.bender.Commit;
import se.fearless.bender.CommitFactory;
import se.fearless.bender.IrcAnnouncer;

import java.io.IOException;

public class SuccessCommand implements Command {
	private final IrcAnnouncer ircAnnouncer;
	private final BuildState buildState;

	public SuccessCommand(IrcAnnouncer ircAnnouncer, BuildState buildState) {
		this.ircAnnouncer = ircAnnouncer;
		this.buildState = buildState;
	}

	@Override
	public void execute(Request request) {
		boolean fail = buildState.isFailure();
		try {
			Commit commit = CommitFactory.create(request);
			buildState.success(commit);
			if (fail) {
				announceFix(commit);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void announceFix(Commit commit) {
		ircAnnouncer.announce(Colors.BLUE + "Commit fixed the build: " + commit);
	}
}
