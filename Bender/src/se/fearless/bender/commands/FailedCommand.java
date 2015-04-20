package se.fearless.bender.commands;

import org.jibble.pircbot.Colors;
import org.simpleframework.http.Request;
import se.fearless.bender.BuildState;
import se.fearless.bender.Commit;
import se.fearless.bender.CommitFactory;
import se.fearless.bender.IrcAnnouncer;

import java.io.IOException;

public class FailedCommand implements Command {
	private final IrcAnnouncer ircAnnouncer;
	private final BuildState buildState;

	public FailedCommand(IrcAnnouncer ircAnnouncer, BuildState buildState) {
		this.ircAnnouncer = ircAnnouncer;
		this.buildState = buildState;
	}

	@Override
	public void execute(Request request) {
		boolean fail = buildState.isFailure();
		try {
			Commit commit = CommitFactory.create(request);
			buildState.fail(commit);
			if (fail) {
				announceStillFail(commit);
			} else {
				announceFirstFail(commit);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void announceFirstFail(Commit commit) {
		ircAnnouncer.announce(Colors.RED + "Commit broke the build: " + commit);
	}

	private void announceStillFail(Commit commit) {
		ircAnnouncer.announce(Colors.RED + "Commit did NOT fix the build: " + commit);
	}
}
