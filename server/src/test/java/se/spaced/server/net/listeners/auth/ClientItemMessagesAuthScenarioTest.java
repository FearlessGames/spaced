package se.spaced.server.net.listeners.auth;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Player;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.ClientConnection;
import se.spaced.shared.model.items.ItemType;

import static se.mockachino.Mockachino.*;


public class ClientItemMessagesAuthScenarioTest extends ScenarioTestBase {
	private ClientConnection clientConnection;
	private Player player;
	private ClientItemMessagesAuth itemMessagesAuth;
	private S2CProtocol playerReceiver;

	@Before
	public void setUp() throws Exception {

		clientConnection = mock(ClientConnection.class);

		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		player = factory.createPlayer("player1");

		playerReceiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(player, playerReceiver);

		itemMessagesAuth = new ClientItemMessagesAuth(clientConnection,
				itemService,
				entityTargetService,
				inventoryService, salvageService);
	}


	@Test
	public void testUseItemTwice() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();

		final UUID uuid = uuidFactory.randomUUID();
		ServerSpell spell = new ServerSpell.Builder("Spellzor").build();
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).spell(spell).build());
		item.setPk(uuid);
		item.setOwner(player);

		Inventory inventory = inventoryService.createInventory(player, 10, InventoryType.BAG);
		inventoryService.add(inventory, item);


		itemMessagesAuth.useItem(item);
		itemMessagesAuth.useItem(item);

		tick(100);
		verifyOnce().on(playerReceiver.combat()).entityStartedSpellCast(player, player, spell);
		verifyOnce().on(playerReceiver.combat()).entityCompletedSpellCast(player, player, spell);
	}
}