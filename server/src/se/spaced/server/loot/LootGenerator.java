package se.spaced.server.loot;

import java.util.Collection;

public interface LootGenerator {
	Collection<Loot> generateLoot();
}
