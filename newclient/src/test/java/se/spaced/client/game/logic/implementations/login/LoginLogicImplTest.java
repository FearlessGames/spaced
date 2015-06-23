package se.spaced.client.game.logic.implementations.login;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.core.states.GameStateContext;
import se.spaced.client.core.states.LoadingState;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.settings.AccountSettings;
import se.spaced.messages.protocol.Salts;
import se.spaced.messages.protocol.c2s.C2SProtocol;
import se.spaced.shared.util.ListenerDispatcher;

import static se.mockachino.Mockachino.*;


public class LoginLogicImplTest {
	private LocalLoginLogicImpl loginLogic;
	private final ListenerDispatcher<ClientEntityListener> entityDispatcher = ListenerDispatcher.create(
			ClientEntityListener.class);

	@Before
	public void setUp() {
		ServerConnection serverConnection = mock(ServerConnection.class);
		C2SProtocol allMessages = MockUtil.deepMock(C2SProtocol.class);
		stubReturn(allMessages).on(serverConnection).getReceiver();
		GameStateContext stateContext = mock(GameStateContext.class);
		LoadingState loadingState = mock(LoadingState.class);
		loginLogic = new LocalLoginLogicImpl(serverConnection,
				stateContext,
				loadingState,
				new AccountSettings());
	}

	//TEST LOCAL LOGIC
	@Test
	@Ignore
	public void testLogin() {
		String name = "accountName";
		String pass = "accountPassword";
		Salts salts = new Salts("userSalt", "oneTimeSalt");
		loginLogic.loginAccount(name, pass, salts);

		// TODO: smrt
		//verifyOnce().on(networkService).sendMessage(argThat(new IsSameLoginMessage(mes)));
	}

	@Test
	public void testLoginCharacter() {
		loginLogic.loginCharacter(new UUID(1l, 1l));
		// TODO: smrt
		//verifyOnce().on(networkService).sendMessage(any(LoginRequestMessage.class));
	}
}
