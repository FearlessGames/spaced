package se.spaced.server.net.listeners.auth;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.uuid.UUID;
import se.mockachino.matchers.matcher.ArgumentCatcher;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Player;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.trade.*;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class ClientTradeMessagesAuthTest extends ScenarioTestBase {
	private ClientConnection clientConnection;
	private ClientTradeMessagesAuth auth;
	private Player player;
	private Player other;
	private S2CProtocol playerReceiver;
	private S2CProtocol otherReceiver;

	@Before
	public void setUp() throws Exception {
		PlayerMockFactory playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		player = playerMockFactory.createPlayer("Player");
		playerReceiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(player, playerReceiver);
		other = playerMockFactory.createPlayer("Other");
		otherReceiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(other, otherReceiver);
		clientConnection = mock(ClientConnection.class);
		when(clientConnection.getReceiver()).thenReturn(playerReceiver);

		SmrtTradeCallback tradeCallback = new SmrtTradeCallback(smrtBroadcaster,
				new TradeExecutor(mock(TransactionManager.class), mock(ItemService.class)));
		TradeService tradeService = new TradeServiceImpl(
				new TradeTransitionModelProvider(tradeCallback));
		auth = new ClientTradeMessagesAuth(clientConnection, tradeService, itemService);
	}

	@Test
	public void testInitiateTradeHappyPath() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		auth.initiateTrade(other);

		ArgumentCatcher<String> checksumCatcher = ArgumentCatcher.create(mAny(String.class));

		verifyOnce().on(playerReceiver.trade()).tradeInitiated(other, match(checksumCatcher));
		String checksum1 = checksumCatcher.getValue();
		verifyOnce().on(otherReceiver.trade()).tradeInitiated(player, match(checksumCatcher));
		String checksum2 = checksumCatcher.getValue();

		assertEquals(checksum1, checksum2);
	}

	@Test
	public void testInitiateWithUnknown() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		auth.initiateTrade(null);
		verifyOnce().on(playerReceiver.trade()).tradeInitFailed(any(String.class));
	}

	@Test(expected = IllegalStateException.class)
	public void testInitiateNoPlayer() throws Exception {
		auth.initiateTrade(other);
	}

	@Test
	public void testAddItem() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		auth.initiateTrade(other);
		ServerItem item = mock(ServerItem.class);
		when(item.getPk()).thenReturn(new UUID(13, 37));
		when(itemService.isOwner(player, item)).thenReturn(true);

		auth.addItemToOffer(item);

		verifyOnce().on(playerReceiver.trade()).itemAdded(player, item, any(String.class));
		verifyOnce().on(otherReceiver.trade()).itemAdded(player, item, any(String.class));
	}

	@Test(expected = IllegalStateException.class)
	public void testAddItemWrongOwner() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		auth.initiateTrade(other);
		ServerItem item = mock(ServerItem.class);

		auth.addItemToOffer(item);
	}
}
