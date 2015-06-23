package se.spaced.server.loot;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import se.fearless.common.uuid.UUID;
import se.spaced.shared.util.random.RandomProvider;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
public class MultiLootTemplate extends PersistableLootTemplate {
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private final Set<LootTemplateProbability> lootTemplates;

	protected MultiLootTemplate() {
		lootTemplates = Collections.emptySet();
	}

	public MultiLootTemplate(
			UUID uuid,
			Set<LootTemplateProbability> lootTemplates,
			String templateName
	) {
		super(uuid, templateName);
		this.lootTemplates = Sets.newHashSet(lootTemplates);
	}

	public MultiLootTemplate(UUID uuid, Set<LootTemplateProbability> lootTemplates) {
		this(uuid, lootTemplates, "templateName");
	}

	public Set<LootTemplateProbability> getLootTemplates() {
		return lootTemplates;
	}

	@Override
	public Collection<Loot> generateLoot(RandomProvider randomProvider) {
		ArrayList<Loot> lootList = Lists.newArrayList();
		for (LootTemplateProbability template : lootTemplates) {
			double prob = template.getProbability();
			if (randomProvider.getDouble(0, 1) <= prob) {
				lootList.addAll(template.getLootTemplate().generateLoot(randomProvider));
			}
		}
		return lootList;
	}
}
