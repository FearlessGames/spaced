package se.spaced.shared.network.webservices.informationservice;

public enum ServerAccountLoadStatus {
	EMTPY,
	NORMAL,
	FULL,
	UNKNOWN;

	private static final int EMPTY_LESS_THEN = 10;
	private static final int NORMAL_LESS_THEN = 100;

	public static ServerAccountLoadStatus getFromNrOfAccounts(int nrOfAccounts) {
		if (nrOfAccounts < EMPTY_LESS_THEN) {
			return ServerAccountLoadStatus.EMTPY;
		} else if (nrOfAccounts < NORMAL_LESS_THEN) {
			return ServerAccountLoadStatus.NORMAL;
		} else {
			return ServerAccountLoadStatus.FULL;
		}
	}
}
