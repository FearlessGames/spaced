package se.spaced.server.loot;

import com.google.common.collect.Lists;
import se.fearless.common.uuid.UUID;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.shared.util.random.RandomProvider;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.Collection;

@Entity
public class MoneyLootTemplate extends PersistableLootTemplate implements LootTemplate {
	@Embedded
	private PersistedMoney money;

	public MoneyLootTemplate() {
	}

	public MoneyLootTemplate(UUID uuid, String templateName) {
		super(uuid, templateName);
	}

	@Override
	public Collection<Loot> generateLoot(RandomProvider randomProvider) {
		return Lists.newArrayList(new Loot(null, money));
	}
}
