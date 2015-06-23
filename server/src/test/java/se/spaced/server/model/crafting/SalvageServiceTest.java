package se.spaced.server.model.crafting;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUIDMockFactory;
import se.mockachino.annotations.Mock;
import se.spaced.server.loot.EmptyLootTemplate;
import se.spaced.server.loot.Loot;
import se.spaced.server.loot.LootAwardService;
import se.spaced.server.loot.LootTemplate;
import se.spaced.server.model.Player;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.shared.util.random.RandomProvider;

import java.util.Collection;
import java.util.List;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.type;

public class SalvageServiceTest {
	@Mock
	private ItemService itemService;
	@Mock
	private TransactionManager transactionManager;
	@Mock
	private LootAwardService lootAwardService;

	@Mock
	private Inventory inventory;
	@Mock
	private ServerItem materialItem;
	@Mock
	private RandomProvider randomProvider;

	private SalvageService salvageService;
	private Player player;


	@Before
	public void setUp() throws Exception {
		setupMocks(this);

		salvageService = new SalvageService(itemService, transactionManager, lootAwardService, randomProvider);
		player = new PlayerMockFactory.Builder(new MockTimeProvider(), new UUIDMockFactory()).build().createPlayer("Player");
	}

	@Test
	public void salvagesItem() throws Exception {
		LootTemplate salvageLootTemplate = createLootTemplate(1);
		List<ServerItem> items = createItem(1, salvageLootTemplate);

		salvageService.salvage(player, items);

		verifyOnce().on(itemService).deleteItem(items.get(0));
		verifyOnce().on(lootAwardService).awardLoot(player, salvageLootTemplate.generateLoot(randomProvider));
	}

	@Test
	public void failsSalvaginMissingSalvageTemplate() {
		List<ServerItem> items = createItem(1, null);

		salvageService.salvage(player, items);

		verifyNever().on(itemService).deleteItem(items.get(0));
		verifyNever().on(lootAwardService).awardLoot(player, type(Collection.class));
	}

	@Test
	public void failsSalvagingEmptyLootTemplate() throws Exception {
		List<ServerItem> items = createItem(1, EmptyLootTemplate.INSTANCE);

		salvageService.salvage(player, items);

		verifyNever().on(itemService).deleteItem(items.get(0));
		verifyNever().on(lootAwardService).awardLoot(player, type(Collection.class));
	}

	@Test
	public void salvagesFullInventory() throws Exception {
		// TODO: What should happen here?
	}

	@Test
	public void salvageOverburdenInventory() throws Exception {
		// TODO: When a salvageable can yeild more than one resulting resource, there is a chance the inv can get full
	}

	private LootTemplate createLootTemplate(int numItems) {
		LootTemplate salvageLootTemplate = mock(LootTemplate.class);
		ServerItemTemplate itemTemplate = mock(ServerItemTemplate.class);
		stubReturn(materialItem).on(itemTemplate).create();

		List<Loot> items = Lists.newArrayList();
		for (int i = 0; i < numItems; i++) {
			items.add(new Loot(itemTemplate));
		}

		stubReturn(items).on(salvageLootTemplate).generateLoot(any(RandomProvider.class));

		return salvageLootTemplate;
	}

	private List<ServerItem> createItem(int numItems, LootTemplate salvageLootTemplate) {
		List<ServerItem> items = Lists.newArrayList();

		for (int i = 0; i < numItems; i++) {
			ServerItem item = mock(ServerItem.class);
			ServerItemTemplate template = mock(ServerItemTemplate.class);

			stubReturn(salvageLootTemplate).on(template).getSalvageLootTemplate();

			stubReturn(template).on(item).getTemplate();

			items.add(item);
		}

		return items;
	}
}
