package se.fearless.bender.commands;

import org.simpleframework.http.Request;
import se.fearless.bender.BuildState;
import se.fearless.bender.IrcAnnouncer;

public class StatusCommand implements Command {
	private final IrcAnnouncer ircAnnouncer;
	private final BuildState buildState;

	public StatusCommand(IrcAnnouncer ircAnnouncer, BuildState buildState) {
		this.ircAnnouncer = ircAnnouncer;
		this.buildState = buildState;
	}

	@Override
	public void execute(Request request) {
		if (!buildState.knownStatus()) {
			ircAnnouncer.announce("Build status is currently unknown.");
		} else if(buildState.isFailure()) {
			ircAnnouncer.announce("Last commit is broken: " + buildState.getLastCommit());
			ircAnnouncer.announce("Broken since: " + buildState.getFirstFail());
		} else {
			ircAnnouncer.announce("Last commit is ok: " + buildState.getLastCommit());
		}
	}
}
