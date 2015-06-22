package se.spaced.server.net.listeners.auth;

public interface AuthCallback {
	void successfullyLoggedIn(String accountName, ExternalAccount account);

	void authenticationFailed(String accountName);

	void basicAuthFailed(String accountName);

	void needsAuthenticator();
}
