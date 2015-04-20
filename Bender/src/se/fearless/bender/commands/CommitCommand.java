package se.fearless.bender.commands;

import org.jibble.pircbot.Colors;
import org.simpleframework.http.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.bender.BuildState;
import se.fearless.bender.Commit;
import se.fearless.bender.CommitFactory;
import se.fearless.bender.CommitLog;
import se.fearless.bender.IrcAnnouncer;
import se.fearless.bender.services.UrlService;

import java.io.IOException;

public class CommitCommand implements Command {
	private static final Logger log = LoggerFactory.getLogger(CommitCommand.class);
	private final IrcAnnouncer ircAnnouncer;
	private final BuildState buildState;
	private final CommitLog commitLog;
	private final UrlService urlService;

	public CommitCommand(IrcAnnouncer ircAnnouncer, BuildState buildState, CommitLog commitLog, UrlService urlService) {
		this.ircAnnouncer = ircAnnouncer;
		this.buildState = buildState;
		this.commitLog = commitLog;
		this.urlService = urlService;
	}

	@Override
	public void execute(Request request) {
		try {
			Commit commit = CommitFactory.create(request);
			buildState.commit(commit);
			commitLog.addCommit(commit);
			announceCommit(commit);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void announceCommit(Commit commit) {
		String url = getUrlForCommit(commit);
		ircAnnouncer.announce(Colors.GREEN + "Commit " + commit + " ( " + url + " ) ");
	}

	private String getUrlForCommit(Commit commit) {
		StringBuilder diffUrl = new StringBuilder();
		try {
			int rev = Integer.parseInt(commit.getRevision());
			int prevRev = rev - 1;
			diffUrl.append("https://flexo.fearlessgames.se/websvn/comp.php?repname=");
			diffUrl.append("Spaced");
			diffUrl.append("&compare[]=%2F@");
			diffUrl.append(prevRev);
			diffUrl.append("&compare[]=%2F@");
			diffUrl.append(rev);

			return urlService.getShortUrl(diffUrl.toString());
		} catch (Exception e) {
			log.error("Failed to get tinyurl for commit, diffUrl: " + diffUrl, e);
			return diffUrl.toString();
		}
	}

}