package se.spaced.server.persistence.util;

public class PageParameters {
	private final int firstResult;
	private final int maxResults;

	public PageParameters(int firstResult, int maxResults) {
		this.firstResult = firstResult;
		this.maxResults = maxResults;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}
}
