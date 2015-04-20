package se.spaced.server.loot;

import com.google.common.collect.Multiset;
import com.google.inject.Inject;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.contribution.ContributionService;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

public class LootDistributionServiceImpl implements LootDistributionService {

	private final ContributionService contributionService;
	private final LootAwardService lootAwardService;

	@Inject
	public LootDistributionServiceImpl(ContributionService contributionService, LootAwardService lootAwardService) {
		this.contributionService = contributionService;
		this.lootAwardService = lootAwardService;
	}

	@Override
	@AutoTransaction
	public void distributeLoot(ServerEntity killedEntity) {
		if (killedEntity instanceof Mob) {
			Mob mobVictim = (Mob) killedEntity;
			Multiset<ServerEntity> contributors = contributionService.getContributors(mobVictim);
			for (ServerEntity contributor : contributors.elementSet()) {
				lootAwardService.awardLoot(contributor, mobVictim.generateLoot());
			}
		}
	}
}
