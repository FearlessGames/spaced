package se.spaced.server.loot;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.shared.util.random.RandomProvider;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Entity
public class KofNLootTemplate extends PersistableLootTemplate {
	private int k;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private final Set<LootTemplateProbability> templates;

	protected KofNLootTemplate() {
		this(null, null, 1, Sets.<LootTemplateProbability>newHashSet());
	}

	public KofNLootTemplate(UUID uuid, String templateName, int k, Set<LootTemplateProbability> templates) {
		super(uuid, templateName);
		this.k = k;
		this.templates = templates;
	}

	public Set<LootTemplateProbability> getTemplates() {
		return templates;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	@Override
	public Collection<Loot> generateLoot(RandomProvider randomProvider) {
		List<LootTemplateProbability> workingCopy = Lists.newLinkedList();
		List<Loot> chosen = Lists.newArrayList();
		double tot = 0;
		for (LootTemplateProbability template : templates) {
			tot += template.getProbability();
			workingCopy.add(template);
		}

		int n = templates.size();
		if (k >= n) {
			for (LootTemplateProbability template : templates) {
				chosen.addAll(template.getLootTemplate().generateLoot(randomProvider));
			}
			return chosen;
		}

		for (int i = 0; i < k; i++) {
			double r = randomProvider.getDouble(0.0, tot);
			Iterator<LootTemplateProbability> iter = workingCopy.iterator();
			while (iter.hasNext()) {
				LootTemplateProbability template = iter.next();
				double weight = template.getProbability();
				r -= weight;
				if (r <= 0) {
					tot -= weight;
					chosen.addAll(template.getLootTemplate().generateLoot(randomProvider));
					iter.remove();
					break;
				}
			}
		}
		return chosen;
	}
}
