package se.fearless.bender;

public class BuildState {

	private Commit firstFail;
	private Commit lastSuccess;
	private Commit lastCommit;

	public Commit fail(Commit commit) {
		if (lastCommit != null && lastCommit.getRevision().equals(commit.getRevision())) {
			commit = lastCommit;
		}

		if(firstFail == null) {
			firstFail = commit;
		}

		return commit;
	}

	public boolean isFailure() {
		return firstFail != null;
	}

	public void commit(Commit commit) {
		lastCommit = commit;
	}

	public Commit success(Commit commit) {
		if (lastCommit != null && lastCommit.getRevision().equals(commit.getRevision())) {
			commit = lastCommit;
		}

		lastSuccess = commit;
		firstFail = null;

		return commit;
	}

	public Commit getFirstFail() {
		return firstFail;
	}

	public Commit getLastCommit() {
		return lastCommit;
	}

	public boolean knownStatus() {
		return lastCommit != null && (lastSuccess != null || firstFail != null);
	}
}
