package se.spaced.server.tools.loot;


import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.server.persistence.dao.interfaces.LootTemplateDao;
import se.spaced.server.persistence.dao.interfaces.SpawnPatternTemplateDao;
import se.spaced.server.tools.loot.simulator.PersistableLootTemplateComparator;

import java.util.List;
import java.util.SortedSet;

@Singleton
public class PersistedLootService {
	private final LootTemplateDao lootTemplateDao;
	private final SpawnPatternTemplateDao spawnPatternTemplateDao;

	@Inject
	public PersistedLootService(
			LootTemplateDao lootTemplateDao,
			SpawnPatternTemplateDao spawnPatternTemplateDao) {
		this.lootTemplateDao = lootTemplateDao;
		this.spawnPatternTemplateDao = spawnPatternTemplateDao;
	}

	public SortedSet<PersistableLootTemplate> getSortedLootTemplates() {

		SortedSet<PersistableLootTemplate> lootTemplates = Sets.newTreeSet(new PersistableLootTemplateComparator());
		List<SpawnPatternTemplate> spawnPatternTemplates = spawnPatternTemplateDao.findAll();

		for (SpawnPatternTemplate spawnPatternTemplate : spawnPatternTemplates) {
			for (MobSpawnTemplate mobSpawnTemplate : spawnPatternTemplate.getMobspawns()) {
				lootTemplates.add(mobSpawnTemplate.getMobTemplate().getLootTemplate());
			}
		}

		List<PersistableLootTemplate> allLootTemplates = lootTemplateDao.findAll();
		lootTemplates.addAll(Collections2.filter(allLootTemplates, new Predicate<PersistableLootTemplate>() {
			@Override
			public boolean apply(PersistableLootTemplate persistableLootTemplate) {
				return persistableLootTemplate.getName() != null;
			}
		}));

		return lootTemplates;
	}
}
