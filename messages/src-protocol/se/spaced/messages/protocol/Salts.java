package se.spaced.messages.protocol;

public class Salts {
	private final String oneTimeSalt;
	private final String userSalt;

	public Salts(String userSalt, String oneTimeSalt) {
		this.oneTimeSalt = oneTimeSalt;
		this.userSalt = userSalt;
	}

	public static Salts fromString(String source) {
		String[] fields = source.split(":");
		return new Salts(fields[0], fields[1]);
	}

	public String getOneTimeSalt() {
		return oneTimeSalt;
	}

	public String getUserSalt() {
		return userSalt;
	}
}
