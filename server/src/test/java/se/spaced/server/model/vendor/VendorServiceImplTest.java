package se.spaced.server.model.vendor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.util.ConcurrentTestHelper;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.mockachino.alias.SimpleAlias;
import se.mockachino.annotations.Mock;
import se.mockachino.matchers.matcher.ArgumentCatcher;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.VendorBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.Player;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.currency.Wallet;
import se.spaced.server.model.currency.WalletCompartment;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.shared.model.items.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.match;
import static se.mockachino.matchers.MatchersBase.mAny;
import static se.spaced.server.model.items.InventoryAssertUtils.assertItemCountInInventory;
import static se.spaced.server.model.items.InventoryAssertUtils.countItemsInInventory;

public class VendorServiceImplTest extends ScenarioTestBase {
	private static final long FOO_AMOUNT = 1000L;
	private VendorBrain vendorBrain;
	@Mock
	private Mob vendor;

	private S2CProtocol playerReciever;
	private Player player;
	private ServerItemTemplate fooTemplate;
	private PersistedCurrency dollars;

	@Before
	public void setUp() {
		setupMocks(this);
		dollars = new PersistedCurrency("Dollar");
		PersistedMoney fooPrice = new PersistedMoney(dollars, FOO_AMOUNT);
		fooTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(), "Foo item", ItemType.TRASH).
				sellsFor(fooPrice).build();

		ArrayList<ServerItemTemplate> itemTypesForSale = Lists.newArrayList(fooTemplate);
		vendorBrain = new VendorBrain(vendor,
				itemTypesForSale,
				mock(MobOrderExecutor.class),
				10000L,
				100.0,
				timeProvider,
				itemService);

		UUIDFactory uuidFactory = UUIDFactoryImpl.INSTANCE;
		UUID value = uuidFactory.combUUID();
		when(vendor.getPk()).thenReturn(value);

		playerReciever = MockUtil.deepMock(S2CProtocol.class);
		player = playerFactory.createPlayer("Alice");
		entityService.addEntity(player, playerReciever);
	}

	@Test
	public void testRegisterAndFetchVendorWarez() {
		vendorService.registerVendor(vendorBrain);
		List<ServerItem> wares = vendorService.getWares(vendor.getPk(), null);
		assertEquals(1, wares.size());
	}


	@Test(expected = IllegalStateException.class)
	public void testDespawningMobRemovesVendorBrain() {
		vendorService.registerVendor(vendorBrain);
		vendorService.entityDespawned(vendor);
		vendorService.getWares(vendor.getPk(), null);
	}

	@Test
	public void buyItemHappyPath() throws Exception {
		moneyService.createWallet(player);
		inventoryService.createInventory(player, 100, InventoryType.BAG);
		moneyService.awardMoney(player, new PersistedMoney(dollars, 30000));
		vendorService.registerVendor(vendorBrain);

		vendorService.initVendoring(player, vendor);
		List<ServerItem> wares = vendorService.getWares(vendorBrain.getMob().getPk(), player);
		ServerItem foo = Iterables.getOnlyElement(wares);
		vendorService.playerBuysItemFromVendor(vendor, player, foo);
		vendorService.endVendoring(player, vendor);

		verifyOnce().on(playerReciever.vendor()).boughtItem(foo);
		ArgumentCatcher<PersistedMoney> moneyCatcher = ArgumentCatcher.create(mAny(PersistedMoney.class));
		verifyOnce().on(moneyService).subtractMoney(match(moneyCatcher), player);
		assertEquals(FOO_AMOUNT, moneyCatcher.getValue().getAmount());
		assertEquals(dollars, moneyCatcher.getValue().getCurrency());

		Inventory inventory = inventoryService.getInventory(player, InventoryType.BAG);

		assertItemCountInInventory(1, inventory);
		assertTrue(inventory.contains(foo));
	}


	@Test
	public void buyWhenCantAfford() throws Exception {
		Wallet wallet = moneyService.createWallet(player);
		inventoryService.createInventory(player, 100, InventoryType.BAG);
		long cashBefore = FOO_AMOUNT / 2;
		moneyService.awardMoney(player, new PersistedMoney(dollars, cashBefore));
		vendorService.registerVendor(vendorBrain);

		vendorService.initVendoring(player, vendor);
		List<ServerItem> wares = vendorService.getWares(vendorBrain.getMob().getPk(), player);
		ServerItem foo = Iterables.getOnlyElement(wares);
		vendorService.playerBuysItemFromVendor(vendor, player, foo);
		vendorService.endVendoring(player, vendor);

		verifyNever().on(playerReciever.vendor()).boughtItem(foo);

		WalletCompartment dollarCompartment = wallet.getCompartment(dollars);
		PersistedMoney moneyAfter = dollarCompartment.getMoney();
		assertEquals(cashBefore, moneyAfter.getAmount());

		Inventory inventory = inventoryService.getInventory(player, InventoryType.BAG);


		assertItemCountInInventory(0, inventory);

		assertFalse(inventory.contains(foo));

		verifyOnce().on(playerReciever.vendor()).cannotAfford(dollars.getName(), FOO_AMOUNT);
	}

	@Test
	public void player1IsOutOfMoneySoLetPlayer2BuyItInstead() throws Exception {
		moneyService.createWallet(player);
		inventoryService.createInventory(player, 100, InventoryType.BAG);
		long cashBefore = FOO_AMOUNT / 2;
		moneyService.awardMoney(player, new PersistedMoney(dollars, cashBefore));
		vendorService.registerVendor(vendorBrain);

		vendorService.initVendoring(player, vendor);
		List<ServerItem> wares = vendorService.getWares(vendorBrain.getMob().getPk(), player);
		ServerItem foo = Iterables.getOnlyElement(wares);
		vendorService.playerBuysItemFromVendor(vendor, player, foo);
		vendorService.endVendoring(player, vendor);


		Player player2 = createPlayer(2);
		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(player2, receiver);
		vendorService.initVendoring(player2, vendor);
		vendorService.playerBuysItemFromVendor(vendor, player2, foo);
		vendorService.endVendoring(player2, vendor);
		verifyOnce().on(receiver.vendor()).boughtItem(foo);
		ArgumentCatcher<PersistedMoney> moneyCatcher = ArgumentCatcher.create(mAny(PersistedMoney.class));
		verifyOnce().on(moneyService).subtractMoney(match(moneyCatcher), player2);
		assertEquals(FOO_AMOUNT, moneyCatcher.getValue().getAmount());
		assertEquals(dollars, moneyCatcher.getValue().getCurrency());

		Inventory inventory = inventoryService.getInventory(player2, InventoryType.BAG);

		assertItemCountInInventory(1, inventory);
		assertTrue(inventory.contains(foo));
	}

	@Test
	public void buyWhenNoSpaceLeft() throws Exception {
		inventoryService.createInventory(player, 0, InventoryType.BAG);
		Wallet wallet = moneyService.createWallet(player);
		long cashBefore = FOO_AMOUNT * 2;
		moneyService.awardMoney(player, new PersistedMoney(dollars, cashBefore));
		vendorService.registerVendor(vendorBrain);

		vendorService.initVendoring(player, vendor);
		List<ServerItem> wares = vendorService.getWares(vendorBrain.getMob().getPk(), player);
		ServerItem foo = Iterables.getOnlyElement(wares);
		vendorService.playerBuysItemFromVendor(vendor, player, foo);
		vendorService.endVendoring(player, vendor);

		verifyNever().on(playerReciever.vendor()).boughtItem(foo);

		WalletCompartment dollarCompartment = wallet.getCompartment(dollars);
		PersistedMoney moneyAfter = dollarCompartment.getMoney();
		assertEquals(cashBefore, moneyAfter.getAmount());

		Inventory inventory = inventoryService.getInventory(player, InventoryType.BAG);

		assertItemCountInInventory(0, inventory);
		assertFalse(inventory.contains(foo));

		verifyOnce().on(playerReciever.vendor()).inventoryFull();
	}

	@Test
	public void buyConcurrently() throws Exception {
		int PLAYERS = 10;
		List<Player> players = Lists.newArrayList();
		List<S2CProtocol> receivers = Lists.newArrayList();

		vendorService.registerVendor(vendorBrain);

		List<ServerItem> wares = vendorService.getWares(vendorBrain.getMob().getPk(), player);
		final ServerItem foo = Iterables.getOnlyElement(wares);

		final ConcurrentTestHelper helper = new ConcurrentTestHelper(PLAYERS);
		final AtomicInteger finished = new AtomicInteger(0);
		for (int i = 0; i < PLAYERS; i++) {
			final Player newPlayer = createPlayer(i);
			S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
			entityService.addEntity(newPlayer, receiver);
			players.add(newPlayer);
			receivers.add(receiver);
			vendorService.initVendoring(newPlayer, vendor);

			Runnable task = new Runnable() {
				@Override
				public void run() {

					try {
						helper.reportReadyToStart();
						helper.awaitGoSignal();
						vendorService.playerBuysItemFromVendor(vendor, newPlayer, foo);
						vendorService.endVendoring(player, vendor);
						finished.incrementAndGet();
					} finally {
						helper.reportFinished();
					}
				}
			};
			Thread thread = new Thread(task);
			thread.start();
		}
		helper.awaitReadyForStart();
		helper.giveGoSignal();
		helper.awaitFinish();
		int boughtCount = 0;
		int wasBoughtCount = 0;
		for (S2CProtocol receiver : receivers) {
			SimpleAlias boughtAlias = newAlias();
			boughtAlias.bind(receiver.vendor()).boughtItem(foo);
			boughtCount += boughtAlias.count();

			SimpleAlias wasBoughtAlias = newAlias();
			wasBoughtAlias.bind(receiver.vendor()).itemWasBought(foo);
			wasBoughtCount += wasBoughtAlias.count();
		}
		assertEquals(1, boughtCount);
		assertEquals(9, wasBoughtCount);

		int totalItemsInBags = 0;
		for (Player p : players) {
			totalItemsInBags += countItemsInInventory(inventoryService.getInventory(p, InventoryType.BAG));
		}
		assertEquals(1, totalItemsInBags);

		assertEquals(PLAYERS, finished.get());
	}

	@Test
	public void buyConcurrentlyTwoItems() throws Exception {
		int PLAYERS = 20;
		final List<Player> players = Lists.newArrayList();
		final List<S2CProtocol> receivers = Lists.newArrayList();

		ServerItemTemplate barTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Bar",
				ItemType.TRASH).build();
		ArrayList<ServerItemTemplate> itemTypesForSale = Lists.newArrayList(fooTemplate, barTemplate);
		vendorBrain = new VendorBrain(vendor,
				itemTypesForSale,
				mock(MobOrderExecutor.class),
				10000L,
				100.0,
				timeProvider,
				itemService);

		vendorService.registerVendor(vendorBrain);

		List<ServerItem> wares = vendorService.getWares(vendorBrain.getMob().getPk(), player);
		final ServerItem foo = Iterables.get(wares, 0);
		final ServerItem bar = Iterables.get(wares, 1);

		final ConcurrentTestHelper helper = new ConcurrentTestHelper(PLAYERS);
		final AtomicInteger finished = new AtomicInteger(0);
		for (int i = 0; i < PLAYERS; i++) {


			final int index = i;
			Runnable task = new Runnable() {
				@Override
				public void run() {

					try {
						final Player newPlayer = createPlayer(index);
						final boolean buyFoo = index % 2 == 0;
						S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
						entityService.addEntity(newPlayer, receiver);
						players.add(newPlayer);
						receivers.add(receiver);
						vendorService.initVendoring(newPlayer, vendor);
						helper.reportReadyToStart();
						helper.awaitGoSignal();
						if (buyFoo) {
							vendorService.playerBuysItemFromVendor(vendor, newPlayer, foo);
						} else {
							vendorService.playerBuysItemFromVendor(vendor, newPlayer, bar);
						}

						finished.incrementAndGet();
					} finally {
						helper.reportFinished();
					}
				}
			};
			Thread thread = new Thread(task);
			thread.start();
		}
		helper.awaitReadyForStart();
		helper.giveGoSignal();
		helper.awaitFinish();
		int boughtFooCount = 0;
		int boughtBarCount = 0;
		int fooWasBoughtCount = 0;
		int barWasBoughtCount = 0;

		for (S2CProtocol receiver : receivers) {
			SimpleAlias fooAlias = newAlias();
			SimpleAlias barAlias = newAlias();
			SimpleAlias fooWasBoughtAlias = newAlias();
			SimpleAlias barWasBoughtAlias = newAlias();
			fooAlias.bind(receiver.vendor()).boughtItem(foo);
			barAlias.bind(receiver.vendor()).boughtItem(bar);
			fooWasBoughtAlias.bind(receiver.vendor()).itemWasBought(foo);
			barWasBoughtAlias.bind(receiver.vendor()).itemWasBought(bar);

			boughtFooCount += fooAlias.count();
			boughtBarCount += barAlias.count();
			fooWasBoughtCount += fooWasBoughtAlias.count();
			barWasBoughtCount += barWasBoughtAlias.count();

		}
		assertEquals(1, boughtFooCount);
		assertEquals(1, boughtBarCount);
		assertEquals(19, fooWasBoughtCount);
		assertEquals(19, barWasBoughtCount);

		int totalItemsInBags = 0;
		for (Player p : players) {
			totalItemsInBags += countItemsInInventory(inventoryService.getInventory(p, InventoryType.BAG));
		}
		assertEquals(2, totalItemsInBags);

		assertEquals(PLAYERS, finished.get());
	}


	private Player createPlayer(int index) {
		Player newPlayer = playerFactory.createPlayer("Player" + index);
		moneyService.createWallet(newPlayer);
		inventoryService.createInventory(newPlayer, 20, InventoryType.BAG);
		moneyService.awardMoney(newPlayer, new PersistedMoney(dollars, 30000));
		return newPlayer;
	}
}
