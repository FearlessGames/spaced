package se.spaced.server.net.listeners.auth;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.time.SystemTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDMockFactory;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.EntityServiceImpl;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.vendor.VendorService;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.List;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.notSame;

public class ClientVendorMessagesAuthTest {

	private ClientConnection clientConnection;
	private ClientVendorMessagesAuth clientVendorMessagesAuth;
	private VendorService vendorService;
	private SmrtBroadcaster<S2CProtocol> broadcaster;
	private Player player;
	private S2CProtocol playerReciever;
	private ServerEntity watto;

	@Before
	public void setUp() throws Exception {
		UUIDMockFactory uuidFactory = new UUIDMockFactory();
		clientConnection = mock(ClientConnection.class);
		vendorService = mock(VendorService.class);
		EntityService entityService = new EntityServiceImpl(uuidFactory, ListenerDispatcher.create(EntityServiceListener.class));
		broadcaster = new SmrtBroadcasterImpl(entityService, mock(CombatRepository.class), mock(VisibilityService.class));
		playerReciever = MockUtil.deepMock(S2CProtocol.class);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		PlayerMockFactory playerFactory = new PlayerMockFactory.Builder(new SystemTimeProvider(),
				uuidFactory).build();
		player = playerFactory.createPlayer("Alice");
		watto = playerFactory.createPlayer("Watto");
		entityService.addEntity(player, playerReciever);
		entityService.addEntity(watto, mock(S2CProtocol.class));
		clientVendorMessagesAuth = new ClientVendorMessagesAuth(clientConnection, vendorService, broadcaster);
	}

	@Test(expected = IllegalStateException.class)
	public void requstStockWhenNotLoggedIn() throws Exception {
		clientVendorMessagesAuth.requestVendorStock(null);
	}

	@Test
	public void requestStockFromUnknownVendor() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		clientVendorMessagesAuth.requestVendorStock(null);

		verifyNever().on(vendorService).initVendoring(any(Player.class), any(Entity.class));
		verifyNever().on(vendorService).getWares(any(UUID.class), any(Player.class));
	}

	@Test
	public void requestStockFromNonVendor() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(vendorService.isVendor(watto)).thenReturn(false);
		clientVendorMessagesAuth.requestVendorStock(watto);

		verifyNever().on(vendorService).initVendoring(any(Player.class), any(Entity.class));
		verifyNever().on(vendorService).getWares(any(UUID.class), any(Player.class));
	}

	@Test
	public void requestStockVendorOutOfReach() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(true);
		moveOutOfRange();
		clientVendorMessagesAuth.requestVendorStock(watto);

		verifyNever().on(vendorService).initVendoring(any(Player.class), any(Entity.class));
		verifyNever().on(vendorService).getWares(any(UUID.class), any(Player.class));
		verifyOnce().on(playerReciever.vendor()).vendorOutOfRange(watto);
	}

	private void moveOutOfRange() {
		player.setPositionalData(new PersistedPositionalData(new SpacedVector3(0, 0, 0), new SpacedRotation(1, 0, 0, 0)));
		watto.setPositionalData(new PersistedPositionalData(new SpacedVector3(100, 0, 0), new SpacedRotation(1, 0, 0, 0)));
	}

	@Test
	public void requestStock() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(true);
		List<ServerItem> warez = Lists.newArrayList(mock(ServerItem.class));
		when(vendorService.getWares(watto.getPk(), player)).thenReturn(warez);
		clientVendorMessagesAuth.requestVendorStock(watto);

		verifyOnce().on(vendorService).initVendoring(player, watto);
		verifyOnce().on(vendorService).getWares(watto.getPk(), player);
		verifyOnce().on(playerReciever.vendor()).vendorStockItems(watto, warez);
	}


	@Test
	public void sellItem() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(true);
		when(vendorService.playerSellsItemToVendor(any(Player.class), any(Entity.class), any(ServerItem.class))).thenReturn(mock(ServerItem.class));

		ServerItem item = mock(ServerItem.class);
		PersistedMoney money = new PersistedMoney(new PersistedCurrency("Dollar"), 1000L);
		when(item.getSellsFor()).thenReturn(money);

		when(vendorService.getCurrentPeopleVendoring(watto)).thenReturn(Sets.<ServerEntity>newHashSet(player));

		clientVendorMessagesAuth.playerSellsItemsToVendor(watto, Lists.newArrayList(item));

		verifyOnce().on(vendorService).playerSellsItemToVendor(player, watto, item);
		verifyOnce().on(playerReciever.vendor()).vendorAddedItem(notSame(item));
	}

	@Test
	public void sellItemNotFound() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(true);
		when(vendorService.playerSellsItemToVendor(any(Player.class), any(Entity.class), any(ServerItem.class))).thenReturn(mock(ServerItem.class));

		ServerItem item = null;

		when(vendorService.getCurrentPeopleVendoring(watto)).thenReturn(Sets.<ServerEntity>newHashSet(player));

		clientVendorMessagesAuth.playerSellsItemsToVendor(watto, Lists.newArrayList(item));

		verifyNever().on(vendorService).playerSellsItemToVendor(player, watto, item);
		verifyNever().on(playerReciever.vendor()).vendorAddedItem(notSame(item));
	}

	@Test
	public void sellItemWithoutPrice() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(true);
		when(vendorService.playerSellsItemToVendor(any(Player.class), any(Entity.class), any(ServerItem.class))).thenReturn(mock(ServerItem.class));

		ServerItem item = mock(ServerItem.class);

		when(vendorService.getCurrentPeopleVendoring(watto)).thenReturn(Sets.<ServerEntity>newHashSet(player));

		clientVendorMessagesAuth.playerSellsItemsToVendor(watto, Lists.newArrayList(item));

		verifyNever().on(vendorService).playerSellsItemToVendor(player, watto, item);
		verifyNever().on(playerReciever.vendor()).vendorAddedItem(notSame(item));
	}

	@Test
	public void buyItem() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(true);

		ServerItem item = mock(ServerItem.class);
		PersistedMoney money = new PersistedMoney(new PersistedCurrency("Dollar"), 1000L);
		when(item.getSellsFor()).thenReturn(money);

		when(vendorService.getCurrentPeopleVendoring(watto)).thenReturn(Sets.<ServerEntity>newHashSet(player));

		clientVendorMessagesAuth.playerBuysItemFromVendor(watto, item);

		verifyOnce().on(vendorService).playerBuysItemFromVendor(watto, player, item);
	}

	@Test(expected = IllegalStateException.class)
	public void buyItemNotLoggedIn() throws Exception {
		when(vendorService.isVendor(watto)).thenReturn(true);
		when(vendorService.playerSellsItemToVendor(any(Player.class), any(Entity.class), any(ServerItem.class))).thenReturn(mock(ServerItem.class));

		ServerItem item = mock(ServerItem.class);
		PersistedMoney money = new PersistedMoney(new PersistedCurrency("Dollar"), 1000L);
		when(item.getSellsFor()).thenReturn(money);

		when(vendorService.getCurrentPeopleVendoring(watto)).thenReturn(Sets.<ServerEntity>newHashSet(player));

		clientVendorMessagesAuth.playerBuysItemFromVendor(watto, item);
	}

	@Test
	public void buyItemNotFoundByCodec() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(true);


		when(vendorService.getCurrentPeopleVendoring(watto)).thenReturn(Sets.<ServerEntity>newHashSet(player));

		clientVendorMessagesAuth.playerBuysItemFromVendor(watto, null);

		verifyNever().on(vendorService).playerBuysItemFromVendor(watto, player, any(ServerItem.class));
		verifyNever().on(playerReciever.vendor()).boughtItem(any(SpacedItem.class));
	}

	@Test
	public void buyItemUnknownVendor() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);

		ServerItem item = mock(ServerItem.class);
		clientVendorMessagesAuth.playerBuysItemFromVendor(null, item);

		verifyNever().on(vendorService).playerBuysItemFromVendor(watto, player, any(ServerItem.class));
		verifyNever().on(playerReciever.vendor()).boughtItem(any(SpacedItem.class));
	}

	@Test
	public void buyItemFromNonVendor() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(false);

		ServerItem item = mock(ServerItem.class);
		clientVendorMessagesAuth.playerBuysItemFromVendor(watto, item);

		verifyNever().on(vendorService).playerBuysItemFromVendor(watto, player, any(ServerItem.class));
		verifyNever().on(playerReciever.vendor()).boughtItem(any(SpacedItem.class));
	}


	@Test
	public void buyItemOutOfRange() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(true);

		ServerItem item = mock(ServerItem.class);
		PersistedMoney money = new PersistedMoney(new PersistedCurrency("Dollar"), 1000L);
		when(item.getSellsFor()).thenReturn(money);
		moveOutOfRange();

		when(vendorService.getCurrentPeopleVendoring(watto)).thenReturn(Sets.<ServerEntity>newHashSet(player));

		clientVendorMessagesAuth.playerBuysItemFromVendor(watto, item);

		verifyOnce().on(playerReciever.vendor()).vendorOutOfRange(watto);
		verifyNever().on(vendorService).playerBuysItemFromVendor(watto, player, item);
		verifyNever().on(playerReciever.vendor()).boughtItem(item);
	}

	@Test
	public void buyItemWithoutPrice() throws Exception {
		when(clientConnection.getPlayer()).thenReturn(player);
		when(clientConnection.getReceiver()).thenReturn(playerReciever);
		when(vendorService.isVendor(watto)).thenReturn(true);

		ServerItem item = mock(ServerItem.class);
		clientVendorMessagesAuth.playerBuysItemFromVendor(watto, item);

		verifyNever().on(vendorService).playerBuysItemFromVendor(watto, player, any(ServerItem.class));
		verifyNever().on(playerReciever.vendor()).boughtItem(any(SpacedItem.class));
	}


}
