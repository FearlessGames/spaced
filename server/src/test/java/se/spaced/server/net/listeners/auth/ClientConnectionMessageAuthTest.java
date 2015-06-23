package se.spaced.server.net.listeners.auth;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.security.Digester;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.mockachino.annotations.Mock;
import se.mockachino.matchers.matcher.ArgumentCatcher;
import se.spaced.messages.protocol.s2c.ServerConnectionMessages;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.account.Account;
import se.spaced.server.account.AccountService;
import se.spaced.server.account.AccountServiceImpl;
import se.spaced.server.account.AccountType;
import se.spaced.server.model.Player;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.player.RemotePlayerService;
import se.spaced.server.model.world.TimeService;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.persistence.dao.interfaces.AccountDao;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class ClientConnectionMessageAuthTest extends ScenarioTestBase {
	private ClientConnectionMessageAuth clientConnectionMessagesAuth;
	private ClientConnection clientConnection;
	private AccountService accountService;
	private ServerConnectionMessages serverConnectionMessages;
	private RemotePlayerService remotePlayerService;
	private Account hiflyersAccount;
	private AuthenticatorService authenticatorService;
	private AccountDao accountDao;
	@Mock
	private TimeService timeService;

	private UUID hiflyerUuid;
	private String address;

	@Before
	public void setup() {
		remotePlayerService = mock(RemotePlayerService.class);
		accountDao = mock(AccountDao.class);
		clientConnection = mock(ClientConnection.class);
		serverConnectionMessages = mock(ServerConnectionMessages.class);
		hiflyersAccount = new Account(UUIDFactoryImpl.INSTANCE.combUUID(), AccountType.REGULAR);
		Digester digester = new Digester("beefcake");
		byte[] bytes = digester.sha512("hiflyer");
		hiflyerUuid = UUID.nameUUIDFromBytes(bytes);
		stubReturn(hiflyersAccount).on(accountDao).findByPk(hiflyerUuid);
		authenticatorService = mock(AuthenticatorService.class);
		accountService = new AccountServiceImpl(accountDao);
		setupMocks(this);
		clientConnectionMessagesAuth = new ClientConnectionMessageAuth(clientConnection,
				accountService,
				serverConnectionMessages,
				remotePlayerService,
				authenticatorService, equipmentService, timeService);
	}

	@Test
	public void loginAccountSetsAccountInClientConnection() {
		clientConnectionMessagesAuth.successfullyLoggedIn("hiflyer",
				new ExternalAccount(hiflyerUuid, AccountType.REGULAR.ordinal()));
		verifyOnce().on(clientConnection).setAccount(hiflyersAccount);
	}

	@Test
	public void loginWithoutAccountCreatesANewIfAuthed() {
		UUID fooUuid = new UUID(123, 456);
		clientConnectionMessagesAuth.successfullyLoggedIn("hiflyer",
				new ExternalAccount(fooUuid, AccountType.REGULAR.ordinal()));
		clientConnectionMessagesAuth.loginAccount("foo", "bar", address);

		ArgumentCatcher<Account> catcher = ArgumentCatcher.create(mAny(Account.class));
		verifyOnce().on(accountDao).persist(match(catcher));
		assertEquals(fooUuid, catcher.getValue().getPk());
	}


	@Test
	public void loginAccountGivesResponseOnSuccess() {
		clientConnectionMessagesAuth.successfullyLoggedIn("hiflyer",
				new ExternalAccount(hiflyerUuid, AccountType.REGULAR.ordinal()));
		verifyOnce().on(serverConnectionMessages).accountLoginResponse("hiflyer", true, any(String.class));
	}

	@Test
	public void loginWithWrongPassword() {
		clientConnectionMessagesAuth.authenticationFailed("hiflyer");
		verifyNever().on(clientConnection).setAccount(hiflyersAccount);
		verifyOnce().on(serverConnectionMessages).accountLoginResponse("hiflyer", eq(false), any(String.class));
	}

	@Test
	public void requestPlayerListWhenNotLoggedIn() {
		clientConnectionMessagesAuth.requestPlayerList();
		verifyNever().on(serverConnectionMessages).playerListResponse(any(List.class));
	}

	@Test
	public void requestPlayerListWhenLoggedIn() {
		Account account = mock(Account.class);
		List<Player> playerList = new ArrayList<Player>();
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		Player player1 = factory.createPlayer("Kalle");
		Player player2 = factory.createPlayer("Pelle");
		playerList.add(player1);
		playerList.add(player2);
		stubReturn(playerList).on(account).getPlayerCharacters();
		stubReturn(account).on(clientConnection).getAccount();
		clientConnectionMessagesAuth.requestPlayerList();
		verifyOnce().on(serverConnectionMessages).playerListResponse(any(List.class));
	}
}
