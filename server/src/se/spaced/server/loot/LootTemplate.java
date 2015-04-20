package se.spaced.server.loot;

import se.spaced.server.persistence.dao.interfaces.Persistable;
import se.spaced.shared.util.random.RandomProvider;

import java.util.Collection;

public interface LootTemplate extends Persistable {
	Collection<Loot> generateLoot(RandomProvider randomProvider);
}
