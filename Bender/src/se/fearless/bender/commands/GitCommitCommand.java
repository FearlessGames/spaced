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

public class GitCommitCommand implements Command {
	private static final Logger log = LoggerFactory.getLogger(CommitCommand.class);
	private final IrcAnnouncer ircAnnouncer;
	private final BuildState buildState;
	private final CommitLog commitLog;
	private final UrlService urlService;

	public GitCommitCommand(CommitLog commitLog, BuildState buildState, IrcAnnouncer ircAnnouncer, UrlService urlService) {
		this.commitLog = commitLog;
		this.buildState = buildState;
		this.ircAnnouncer = ircAnnouncer;
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
		String message = String.format("%s: commit by %s: %s", commit.getProject(), commit.getAuthor(), commit.getCommitMessage());
		ircAnnouncer.announce(Colors.GREEN + message + " ( " + url + " ) ");
	}

	private String getUrlForCommit(Commit commit) {
		StringBuilder historyUrl = new StringBuilder();
		try {

			historyUrl.append("http://flexo.fearlessgames.se/gitweb/?p=");
			historyUrl.append(commit.getProject()).append(".git");
			historyUrl.append(";a=commitdiff");
			historyUrl.append(";h=");
			historyUrl.append(commit.getRevision());

			return urlService.getShortUrl(historyUrl.toString());
		} catch (Exception e) {
			log.error("Failed to get tinyurl for commit, historyUrl: " + historyUrl, e);
			return historyUrl.toString();
		}
	}
}


