package se.spaced.server.model.player;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.uuid.UUID;
import se.mockachino.CallHandler;
import se.mockachino.MethodCall;
import se.mockachino.annotations.Mock;
import se.mockachino.matchers.Matchers;
import se.mockachino.matchers.matcher.ArgumentCatcher;
import se.spaced.messages.protocol.InventoryData;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.net.ClientConnectionImpl;
import se.spaced.server.services.PlayerConnectedService;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.eq;


public class RemotePlayerServiceImplTest extends ScenarioTestBase {
	@Mock
	private PlayerConnectedService playerConnectedService;

	private RemotePlayerServiceImpl remotePlayerService;

	@Before
	public void setup() {
		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				return new EquippedItems(((ServerEntity) call.getArguments()[0]));
			}
		}).on(equipmentService).getEquippedItems(any(ServerEntity.class));
		remotePlayerService = new RemotePlayerServiceImpl(entityService,
				playerConnectedService,
				playerService,
				spellService,
				smrtBroadcaster,
				inventoryService,
				equipmentService, moneyService);
	}

	@Test
	public void testPlayerLoggedIn() {

		PlayerMockFactory playerFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		Player player = playerFactory.createPlayer("Kalle");


		Player alreadyOnlinePlayer = playerFactory.createPlayer("Bengt");

		S2CProtocol alreadyOnlineReceiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(alreadyOnlinePlayer, alreadyOnlineReceiver);

		ClientConnectionImpl client = mock(ClientConnectionImpl.class);
		S2CProtocol entityReceiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(entityReceiver).on(client).getReceiver();

		remotePlayerService.playerLoggedIn(player, client);

		verifyOnce().on(client).setPlayer(player);

		ArgumentCatcher<EntityData> argument = ArgumentCatcher.create(Matchers.mAny(EntityData.class));
		verifyOnce().on(entityReceiver.connection()).playerLoginResponse(eq(true),
				any(String.class),
				Matchers.match(argument),
				Matchers.any(
						Map.class), Matchers.eq(player.isGm()));
		assertEquals("Kalle", argument.getValue().getName());
		verifyOnce().on(entityReceiver.entity()).entityAppeared(alreadyOnlinePlayer,
				alreadyOnlinePlayer.createEntityData(),
				any(Map.class));
		verifyNever().on(entityReceiver.connection()).playerLoggedIn(any(EntityData.class));

		verifyOnce().on(alreadyOnlineReceiver.connection()).playerLoggedIn(Matchers.match(argument));
		assertEquals("Kalle", argument.getValue().getName());

		verifyOnce().on(playerConnectedService).addConnectedPlayer(player);
	}

	@Test
	public void testPlayerLoggedTwice() {

		PlayerMockFactory playerFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		Player player = playerFactory.createPlayer("Kalle");

		Player alreadyOnlinePlayer = playerFactory.createPlayer("Bengt");
		S2CProtocol alreadyOnlineReceiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(alreadyOnlinePlayer, alreadyOnlineReceiver);

		ClientConnectionImpl client = mock(ClientConnectionImpl.class);
		S2CProtocol entityReceiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(entityReceiver).on(client).getReceiver();

		remotePlayerService.playerLoggedIn(player, client);

		verifyOnce().on(client).setPlayer(player);

		ArgumentCatcher<EntityData> argument = ArgumentCatcher.create(Matchers.mAny(EntityData.class));
		verifyOnce().on(entityReceiver.connection()).playerLoginResponse(eq(true),
				any(String.class),
				Matchers.match(argument),
				Matchers.any(
						Map.class), Matchers.eq(player.isGm()));
		assertEquals("Kalle", argument.getValue().getName());
		verifyOnce().on(entityReceiver.entity()).entityAppeared(alreadyOnlinePlayer,
				alreadyOnlinePlayer.createEntityData(),
				any(Map.class));
		verifyNever().on(entityReceiver.connection()).playerLoggedIn(any(EntityData.class));

		verifyOnce().on(alreadyOnlineReceiver.connection()).playerLoggedIn(Matchers.match(argument));
		assertEquals("Kalle", argument.getValue().getName());

		verifyOnce().on(playerConnectedService).addConnectedPlayer(player);

		remotePlayerService.playerLoggedOut(player, client);
		verifyOnce().on(entityReceiver.connection()).logoutResponse();
		verifyOnce().on(alreadyOnlineReceiver.connection()).playerDisconnected(player, player.getName());
		verifyOnce().on(playerConnectedService).removeConnectedPlayer(player);

		getData(entityReceiver.entity()).resetCalls();
		getData(alreadyOnlineReceiver.connection()).resetCalls();
		getData(playerConnectedService).resetCalls();

		remotePlayerService.playerLoggedIn(player, client);
		verifyOnce().on(alreadyOnlineReceiver.connection()).playerLoggedIn(Matchers.match(argument));
		assertEquals("Kalle", argument.getValue().getName());

		verifyOnce().on(entityReceiver.entity()).entityAppeared(alreadyOnlinePlayer,
				alreadyOnlinePlayer.createEntityData(),
				any(Map.class));

		verifyOnce().on(playerConnectedService).addConnectedPlayer(player);
	}

	@Test
	public void testGetInventoryAtLogin() throws Exception {
		PlayerMockFactory playerFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		Player player = playerFactory.createPlayer("Kalle");

		ClientConnectionImpl client = mock(ClientConnectionImpl.class);
		S2CProtocol entityReceiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(entityReceiver).on(client).getReceiver();
		se.spaced.server.model.items.Inventory inventory = inventoryService.createInventory(player,
				10,
				InventoryType.BAG);
		ServerItemTemplate itemTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Thingy",
				ItemType.HELMET).build();
		ServerItem thing = new ServerItem(itemTemplate);
		UUID uuid = new UUID(123L, 456L);
		thing.setPk(uuid);
		thing.setOwner(player);
		inventoryService.add(inventory, thing);

		remotePlayerService.playerLoggedIn(player, client);

		ArgumentCatcher<InventoryData> argument = ArgumentCatcher.create(Matchers.mAny(InventoryData.class));
		verifyOnce().on(entityReceiver.item()).sendInventory(Matchers.match(argument));

		assertTrue(argument.getValue().getItemMap().containsValue(thing));
	}

	@Test
	public void testPlayerLoggedOut() {
		remotePlayerService = new RemotePlayerServiceImpl(entityService,
				playerConnectedService,
				playerService,
				spellService,
				smrtBroadcaster,
				inventoryService,
				equipmentService, moneyService);
		ClientConnectionImpl client = mock(ClientConnectionImpl.class);

		UUID uuid = uuidFactory.randomUUID();
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		Player player = factory.createPlayer("Kalle");
		stubReturn(player).on(entityService).getEntity(uuid);

		remotePlayerService.playerLoggedOut(player, client);
		verifyOnce().on(entityService).removeEntity(player);
		verifyOnce().on(playerConnectedService).removeConnectedPlayer(player);
	}
}
