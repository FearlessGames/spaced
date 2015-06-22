package se.spaced.server.tools.loot.simulator;

import se.spaced.server.loot.PersistableLootTemplate;

import java.util.Comparator;

public class PersistableLootTemplateComparator implements Comparator<PersistableLootTemplate> {
	@Override
	public int compare(PersistableLootTemplate o1, PersistableLootTemplate o2) {
		String name1 = o1.getName();
		String name2 = o2.getName();
		if (name1 == null && name2 == null) {
			return o1.getPk().compareTo(o2.getPk());
		}
		if (name1 == null) {
			return 1;
		} else if (name2 == null) {
			return -1;
		}
		return name1.compareTo(name2);
	}
}
