package se.spaced.server.loot;

import se.spaced.server.model.ServerEntity;

public interface LootDistributionService {
	void distributeLoot(ServerEntity killedEntity);
}
