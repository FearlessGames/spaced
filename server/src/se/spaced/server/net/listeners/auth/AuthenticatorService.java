package se.spaced.server.net.listeners.auth;

public interface AuthenticatorService {
	void authenticate(String accountName, String hash, String address, AuthCallback callback);

	void requestAuthSalts(String userName, AuthSaltsCallback callback);

	void authenticateWithAuthenticator(String accountName, String hash, String address, String key, AuthCallback authCallback);
}
