package se.spaced.client.game.logic.implementations.login;

import com.google.inject.Inject;
import se.fearless.common.security.BCrypter;
import se.fearless.common.security.Digester;
import se.fearless.common.uuid.UUID;
import se.spaced.client.core.states.GameStateContext;
import se.spaced.client.core.states.LoadingState;
import se.spaced.client.game.logic.local.LocalLoginLogic;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.settings.AccountSettings;
import se.spaced.messages.protocol.Salts;

import java.net.InetSocketAddress;

public class LocalLoginLogicImpl implements LocalLoginLogic {
	private final ServerConnection serverConnection;
	private final GameStateContext stateContext;
	private final LoadingState loadingState;
	private final AccountSettings accountSettings;

	@Inject
	public LocalLoginLogicImpl(
			ServerConnection serverConnection,
			GameStateContext stateContext,
			LoadingState loadingState,
			AccountSettings accountSettings) {
		this.serverConnection = serverConnection;
		this.stateContext = stateContext;
		this.loadingState = loadingState;
		this.accountSettings = accountSettings;
	}

	@Override
	public void loginAccount(final String accountName, String password, Salts authSalts) {
		AuthenticateData authenticateData = new AuthenticateData(accountName, password, authSalts).invoke();
		serverConnection.getReceiver().connection().loginAccount(accountName,
				authenticateData.getDigesterPassword(), authenticateData.getAddress());

	}

	@Override
	public void authenticateAccount(String accountName, String password, Salts authSalts, String key) {
		AuthenticateData authenticateData = new AuthenticateData(accountName, password, authSalts).invoke();
		serverConnection.getReceiver().connection().authenticateAccount(accountName,
				authenticateData.getDigesterPassword(), authenticateData.getAddress(), key);
	}

	@Override
	public void requestAuthSalts(String userName) {
		serverConnection.getReceiver().connection().requestLoginSalts(userName);
	}

	@Override
	public void loginCharacter(UUID playerId) {
		serverConnection.getReceiver().connection().getLocation(playerId);
		loadingState.setPosition(null);
		stateContext.changeState(loadingState);
	}

	private class AuthenticateData {
		private String accountName;
		private String password;
		private Salts authSalts;
		private String address;
		private String digesterPassword;

		public AuthenticateData(String accountName, String password, Salts authSalts) {
			this.accountName = accountName;
			this.password = password;
			this.authSalts = authSalts;
		}

		public String getAddress() {
			return address;
		}

		public String getDigesterPassword() {
			return digesterPassword;
		}

		public AuthenticateData invoke() {
			Digester digester = new Digester("beefcake");
			accountSettings.setAccount(accountName);
			if (!password.equals(AccountSettings.USE_OLD_PASSWORD)) {
				accountSettings.setHashedPassword(BCrypter.bcrypt(accountName + password, authSalts.getUserSalt()));
			}
			final InetSocketAddress localAddress = serverConnection.getLocalAddress();
			address = localAddress.getAddress().getHostAddress();
			password = accountSettings.getHashedPassword();

			digesterPassword = digester.sha512Hex(password + authSalts.getOneTimeSalt());
			return this;
		}
	}
}
