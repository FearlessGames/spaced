package se.spaced.messages.protocol;

public class AuthToken {
	private final long id;
	private final String token;

	public static AuthToken fromString(String string) {
		String[] strings = string.split(":");
		return new AuthToken(Long.parseLong(strings[0]), strings[1]);
	}

	public AuthToken(long id, String token) {
		this.id = id;
		this.token = token;
	}

	public long getId() {
		return id;
	}

	public String getToken() {
		return token;
	}
}