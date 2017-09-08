package se.spaced.messages.protocol;

public class Password {
	private final String password;

	public Password(String password) {
		this.password = password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Password)) {
			return false;
		}

		Password password1 = (Password) o;

		return password.equals(password1.password);
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "<password>";
	}

	public String getPasswordString() {
		return password;
	}
}
