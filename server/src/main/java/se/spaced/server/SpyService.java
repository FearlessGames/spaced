package se.spaced.server;

import com.google.inject.Inject;
import se.spaced.server.loot.LootDistributionService;
import se.spaced.server.stats.KillStatisticsCollector;
import se.spaced.server.stats.SpellStatisticsCollector;

public class SpyService {

	private final KillStatisticsCollector killStatisticsCollector;
	private final SpellStatisticsCollector spellStatisticsCollector;
	private final LootDistributionService lootDistributionService;


	@Inject
	public SpyService(KillStatisticsCollector killStatisticsCollector,
			SpellStatisticsCollector spellStatisticsCollector,
			LootDistributionService lootDistributionService) {
		this.killStatisticsCollector = killStatisticsCollector;
		this.spellStatisticsCollector = spellStatisticsCollector;
		this.lootDistributionService = lootDistributionService;
	}
}
