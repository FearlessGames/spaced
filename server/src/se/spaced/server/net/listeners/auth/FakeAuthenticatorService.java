package se.spaced.server.net.listeners.auth;

import com.google.inject.Inject;
import se.fearlessgames.common.util.Digester;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.Salts;

public class FakeAuthenticatorService implements AuthenticatorService {

	private static final boolean FAKE_AUTHENTICATOR = true;

	@Inject
	public FakeAuthenticatorService() {

	}

	private final Digester digester = new Digester("beefcake");

	@Override
	public void authenticate(String accountName, String password, String address, AuthCallback callback) {
		final byte[] bytes = digester.md5(accountName);
		if (FAKE_AUTHENTICATOR) {
			callback.needsAuthenticator();
		} else {
			callback.successfullyLoggedIn(accountName, new ExternalAccount(UUID.nameUUIDFromBytes(bytes), 0));
		}
	}

	@Override
	public void requestAuthSalts(String userName, AuthSaltsCallback callback) {
		callback.receievedSalts(new Salts("$2a$10$teq7mkJfiJWukimg8Z8X/O", "flingsalt"));
	}

	@Override
	public void authenticateWithAuthenticator(String accountName, String hash, String address, String key, AuthCallback authCallback) {
		final byte[] bytes = digester.md5(accountName);
		authCallback.successfullyLoggedIn(accountName, new ExternalAccount(UUID.nameUUIDFromBytes(bytes), 0));
	}
}
