package se.spaced.server.model.crafting;

import com.google.inject.Inject;
import se.spaced.server.loot.EmptyLootTemplate;
import se.spaced.server.loot.LootAwardService;
import se.spaced.server.loot.LootTemplate;
import se.spaced.server.model.Player;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.shared.util.random.RandomProvider;

public class SalvageService {
	private final ItemService itemService;
	private final TransactionManager transactionManager;
	private final LootAwardService lootAwardService;
	private final RandomProvider randomProvider;

	@Inject
	public SalvageService(
			ItemService itemService,
			TransactionManager transactionManager,
			LootAwardService lootAwardService, RandomProvider randomProvider) {
		this.itemService = itemService;
		this.transactionManager = transactionManager;
		this.lootAwardService = lootAwardService;
		this.randomProvider = randomProvider;
	}

	@AutoTransaction
	public void salvage(final Player player, final Iterable<ServerItem> items) {
		for (ServerItem item : items) {
			final ServerItemTemplate template = item.getTemplate();
			transactionManager.rebuildFromDataBase(template);

			final LootTemplate salvageTemplate = template.getSalvageLootTemplate();

			if (salvageTemplate != null && salvageTemplate != EmptyLootTemplate.INSTANCE) {
				itemService.deleteItem(item);
				lootAwardService.awardLoot(player, salvageTemplate.generateLoot(randomProvider));
			}
		}
	}
}
