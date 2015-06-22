package se.spaced.server.net.listeners.auth;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.crafting.SalvageService;
import se.spaced.server.model.items.ItemTemplateDataFactory;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.net.ClientConnection;
import se.spaced.shared.model.items.ItemType;

import java.util.List;

import static org.junit.Assert.fail;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class ClientItemMessagesAuthTest extends ScenarioTestBase {
	private ClientConnection clientConnection;
	private Player player;
	private ClientItemMessagesAuth itemMessagesAuth;
	private SalvageService salvageService;

	@Before
	public void setUp() throws Exception {
		clientConnection = mock(ClientConnection.class);
		player = mock(Player.class);
		when(player.getPk()).thenReturn(new UUID(0, 1));
		salvageService = mock(SalvageService.class);

		itemMessagesAuth = new ClientItemMessagesAuth(clientConnection,
				itemService,

				entityTargetService,
				inventoryService, salvageService);
	}

	@Test
	public void testUseItemNotFound() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();

		final UUID uuid = uuidFactory.randomUUID();
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);
		stubReturn(false).on(itemService).isOwner(player, item);

		itemMessagesAuth.useItem(item);
		verifyNever().on(itemService).useItem(any(ServerEntity.class), any(ServerEntity.class), any(ServerItem.class));
	}


	@Test
	public void testUseItemWrongOwner() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();
		final UUID uuid = uuidFactory.randomUUID();
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);
		stubReturn(false).on(itemService).isOwner(player, item);

		itemMessagesAuth.useItem(item);
		verifyNever().on(itemService).useItem(any(ServerEntity.class), any(ServerEntity.class), any(ServerItem.class));
	}

	@Test
	public void testUseItem() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();
		stubReturn(true).on(player).isAlive();
		final UUID uuid = uuidFactory.randomUUID();
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);

		stubReturn(true).on(itemService).isOwner(player, item);

		itemMessagesAuth.useItem(item);
		verifyExactly(1).on(itemService).useItem(player, player, item);
	}


	@Test(expected = IllegalStateException.class)
	public void testUseItemNoPlayer() throws Exception {
		final UUID uuid = uuidFactory.randomUUID();
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);

		itemMessagesAuth.useItem(item);
	}

	@Test
	public void testUseNullItem() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();
		stubReturn(true).on(player).isAlive();

		try {
			itemMessagesAuth.useItem(null);
		} catch (Exception e) {
			fail();
		}
		verifyNever().on(itemService).useItem(player, player, null);
	}

	@Test
	public void requestItemTemplateData() {
		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		stubReturn(receiver).on(clientConnection).getReceiver();
		stubReturn(player).on(clientConnection).getPlayer();

		UUID itemTemplatePk = new UUID(123L, 987L);
		ServerItemTemplate template = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Foo",
				ItemType.GLOVES).build();
		template.setPk(itemTemplatePk);
		ItemTemplateData data = ItemTemplateDataFactory.create(template);
		stubReturn(template).on(itemService).getTemplateByPk(itemTemplatePk);
		itemMessagesAuth.requestItemTemplateData(itemTemplatePk);
		verifyOnce().on(receiver.item()).itemTemplateDataResponse(data);
	}

	@Test
	public void testDeleteItem() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();
		stubReturn(true).on(player).isAlive();
		final UUID uuid = uuidFactory.randomUUID();
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);

		stubReturn(true).on(itemService).isOwner(player, item);

		itemMessagesAuth.deleteItem(item);
		verifyExactly(1).on(itemService).deleteItem(item);
	}

	@Test
	public void testDeleteItemWrongOwner() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();
		final UUID uuid = uuidFactory.randomUUID();
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);


		stubReturn(false).on(itemService).isOwner(player, item);

		itemMessagesAuth.deleteItem(item);
		verifyNever().on(itemService).deleteItem(any(ServerItem.class));
	}

	@Test
	public void salvageItemHappyPath() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();
		final UUID uuid = uuidFactory.randomUUID();
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);


		stubReturn(true).on(itemService).isOwner(player, item);

		itemMessagesAuth.salvageItem(Lists.<SpacedItem>newArrayList(item));
		verifyOnce().on(salvageService).salvage(player, Lists.<ServerItem>newArrayList(item));
	}

	@Test
	public void salvageItemNotOwned() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();
		final UUID uuid = uuidFactory.randomUUID();
		ServerItem item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());
		item.setPk(uuid);


		stubReturn(false).on(itemService).isOwner(player, item);

		try {
			itemMessagesAuth.salvageItem(Lists.<SpacedItem>newArrayList(item));
			fail("Should get exception");
		} catch (IllegalStateException e) {

		}
		verifyNever().on(salvageService).salvage(player, Lists.<ServerItem>newArrayList(item));
	}

	@Test
	public void salvageNoItems() throws Exception {
		stubReturn(player).on(clientConnection).getPlayer();
		try {
			itemMessagesAuth.salvageItem(Lists.<SpacedItem>newArrayList());
			fail("Should throw exception");
		} catch (IllegalStateException e) {
		}
		verifyNever().on(salvageService).salvage(player, any(List.class));
	}

	@Test
	public void salvageItemWhenNotInGame() throws Exception {
		ServerItem item = null;
		try {
			final UUID uuid = uuidFactory.randomUUID();
			item = new ServerItem(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
					"Can of Slurm",
					ItemType.CONSUMABLE).build());
			item.setPk(uuid);


			stubReturn(false).on(itemService).isOwner(player, item);

			itemMessagesAuth.salvageItem(Lists.<SpacedItem>newArrayList(item));
			fail("Should throw exception");
		} catch (IllegalStateException e) {
		}
		verifyNever().on(salvageService).salvage(player, Lists.<ServerItem>newArrayList(item));
	}


}
