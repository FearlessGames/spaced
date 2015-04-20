package se.fearless.bender;

import java.util.LinkedList;
import java.util.List;

public class CommitLog {
	//TODO: probably needs concurrency. 
	LinkedList<Commit> commitLog = new LinkedList<Commit>();
	private final int queueSize;

	public CommitLog(int queueSize) {
		this.queueSize = queueSize;
	}

	public void addCommit(Commit commit) {
		if (commitLog.size() >= queueSize) {
			commitLog.removeFirst();
		}
		commitLog.add(commit);
	}

	public List<Commit> getLast(int numberOfEntriesToFetch) {
		if (numberOfEntriesToFetch > commitLog.size()) {
			numberOfEntriesToFetch = commitLog.size();
		}
		return commitLog.subList(commitLog.size() - numberOfEntriesToFetch, commitLog.size());
	}
}
