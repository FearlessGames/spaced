package se.fearless.bender;

public class Commit {
	private final String project;
	private final String author;
	private final String revision;
	private final String commitMessage;

	public Commit(String project, String author, String revision, String commitMessage) {
		this.project = project;
		this.author = author;
		this.revision = revision;
		this.commitMessage = commitMessage;
	}

	public String getAuthor() {
		return author;
	}

	public String getRevision() {
		return revision;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public String getProject() {
		return project;
	}

	@Override
	public String toString() {
		return String.format("revision %s by %s: %s", revision, author, commitMessage);
	}
}
