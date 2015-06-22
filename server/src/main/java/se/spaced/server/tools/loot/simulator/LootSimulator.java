package se.spaced.server.tools.loot.simulator;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import se.spaced.server.loot.Loot;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LootSimulator {
	private final PersistableLootTemplate lootTemplate;
	private final RandomProvider randomProvider;
	private final Multiset<ServerItemTemplate> items = HashMultiset.create();
	private int minItems = Integer.MAX_VALUE;
	private int maxItems = 0;
	private int totalItems = 0;
	private double[] lootAmounts;

	public LootSimulator(PersistableLootTemplate lootTemplate) {
		this.lootTemplate = lootTemplate;
		randomProvider = new RealRandomProvider();
	}

	public void simulate(int times) {
		lootAmounts = new double[times];
		for (int i = 0; i < times; i++) {
			lootAmounts[i] = singleRun();
		}
	}

	private int singleRun() {
		Collection<Loot> loots = lootTemplate.generateLoot(randomProvider);
		int numberOfItems = loots.size();
		minItems = Math.min(minItems, numberOfItems);
		maxItems = Math.max(maxItems, numberOfItems);
		totalItems += numberOfItems;
		for (Loot loot : loots) {
			items.add(loot.getItemTemplate());
		}
		return numberOfItems;
	}

	public List<LootResult> getItems() {
		List<LootResult> result = Lists.newArrayList();
		for (ServerItemTemplate item : items.elementSet()) {
			int count = items.count(item);
			result.add(new LootResult(item, count));
		}
		Collections.sort(result);
		return result;
	}

	public int getMinItems() {
		return minItems;
	}

	public int getMaxItems() {
		return maxItems;
	}

	public double[] getLootAmounts() {
		return lootAmounts;
	}
}
