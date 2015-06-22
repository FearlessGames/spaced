package se.spaced.server.loot;

import se.spaced.server.model.ServerEntity;

import java.util.Collection;

public interface LootAwardService {
	void awardLoot(ServerEntity receiver, Collection<Loot> loots);
}
