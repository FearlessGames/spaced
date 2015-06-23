package se.spaced.server.net.listeners.auth;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.mock.MockUtil;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.items.*;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.migrator.ServerContentPopulator;
import se.spaced.server.services.GameMasterService;
import se.spaced.server.services.GameMasterServiceImpl;
import se.spaced.server.spell.SpellService;
import se.spaced.shared.model.items.ItemType;

import static org.junit.Assert.fail;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.anyInt;

public class GameMasterApiImplTest extends ScenarioTestBase {
	private Player gmEntity;
	private ServerEntity krka;
	private S2CProtocol gmReceiver;
	private GameMasterApiImpl gmApiImpl;
	private S2CProtocol krkaReceiver;
	private ClientConnection gmClientConnection;

	@Before
	public void setup() {
		SpacedRotation gmRotation = new SpacedRotation(2, 4, 3, 1, true);

		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();

		gmEntity = factory.createPlayer("Gm");
		gmEntity.setPositionalData(new PersistedPositionalData(new SpacedVector3(10, 20, 30), gmRotation));

		SpacedRotation krkaRotation = new SpacedRotation(2, 4, 3, 4, true);

		krka = factory.createPlayer("krka");
		krka.setPositionalData(new PersistedPositionalData(new SpacedVector3(1000, 2000, 3000), krkaRotation));

		gmReceiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(gmEntity, gmReceiver);
		krkaReceiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(krka, krkaReceiver);

		gmClientConnection = mock(ClientConnection.class);
		stubReturn(gmEntity).on(gmClientConnection).getPlayer();
		stubReturn(gmReceiver).on(gmClientConnection).getReceiver();
		ActionScheduler actionScheduler = new ActionScheduler();
		TransactionManager transactionManager = MockUtil.deepMock(TransactionManager.class);
		SpellService spellService = mock(SpellService.class);
		GameMasterService gameMasterService = new GameMasterServiceImpl(movementService,
				timeProvider,
				inventoryService,
				itemService,
				smrtBroadcaster,
				spawnService,
				actionScheduler,
				entityService,
				uuidFactory,
				mobOrderExecutor,
				mock(GmMobLifecycle.class),
				mock(ServerContentPopulator.class),
				spellService,
				moneyService,
				randomProvider);
		gmApiImpl = new GameMasterApiImpl(entityService, gmClientConnection, smrtBroadcaster,
				itemService,
				null,
				transactionManager,
				null, spellService, moneyService, gameMasterService);
	}

	@Test
	public void visitPlayer() {
		gmApiImpl.visit(gmEntity, "krka");
		verifyOnce().on(gmReceiver.movement()).teleportTo(krka.getPositionalData().toPositionalData());
	}


	@Test
	public void visitUnknown() {
		gmApiImpl.visit(gmEntity, "foo");
		verifyOnce().on(gmReceiver.entity()).unknownEntityName("foo");
	}

	@Test
	public void visitWithoutPlayerSetOnConnection() {
		stubReturn(null).on(gmClientConnection).getPlayer();
		try {
			gmApiImpl.visit(gmEntity, "krka");
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void summonPlayer() throws Exception {
		gmApiImpl.summonEntity(gmEntity, "krka");
		verifyOnce().on(krkaReceiver.movement()).teleportTo(gmEntity.getPositionalData().toPositionalData());
	}

	@Test
	public void summonNonExistingPlayer() throws Exception {
		gmApiImpl.summonEntity(gmEntity, "foo");
		verifyOnce().on(gmReceiver.gamemaster()).failureNotification(any(String.class));
	}

	@Test
	public void getItemGmStyle() throws InventoryFullException {
		ServerItemTemplate itemTemplate = new ServerItemTemplate.Builder(uuidFactory.randomUUID(),
				"Item",
				ItemType.CONSUMABLE).build();
		Inventory inv = inventoryService.createInventory(gmEntity, 100, InventoryType.BAG);

		stubReturn(itemTemplate).on(itemService).getTemplateByName("Foo");
		gmApiImpl.giveItem(gmEntity, gmEntity.getName(), "Foo", 3);

		verifyExactly(3).on(gmReceiver.item()).itemAdded(inv, anyInt(), any(SpacedItem.class));
		verifyExactly(3).on(inventoryService).add(inv, any(ServerItem.class));
	}
}
