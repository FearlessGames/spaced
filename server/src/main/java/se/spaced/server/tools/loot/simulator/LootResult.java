package se.spaced.server.tools.loot.simulator;

import se.spaced.server.model.items.ServerItemTemplate;

public class LootResult implements Comparable<LootResult> {
	private final ServerItemTemplate item;
	private final int count;

	public LootResult(ServerItemTemplate item, int count) {
		this.item = item;
		this.count = count;
	}

	public ServerItemTemplate getItem() {
		return item;
	}

	public int getCount() {
		return count;
	}

	@Override
	public int compareTo(LootResult o) {
		return o.count - count;
	}
}
