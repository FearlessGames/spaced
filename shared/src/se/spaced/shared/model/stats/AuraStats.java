package se.spaced.shared.model.stats;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import se.spaced.shared.model.aura.ModStat;

public class AuraStats extends DerivedStat implements MutableStat {
	private final Multiset<ModStat> modStats = TreeMultiset.create();
	private final MutableStat base;

	protected AuraStats(MutableStat base) {
		super("AuraStat:" + base.getName(), base);
		this.base = base;
		update(this);
	}

	@Override
	protected double recalculateValue() {
		double baseValue = base.getValue();
		for (ModStat modStat : modStats) {
			baseValue = modStat.getOperator().perform(baseValue, modStat.getValue());
		}
		return baseValue;
	}

	public void addModStat(ModStat modStat) {
		modStats.add(modStat);
		update(this);
	}

	public void removeModStat(ModStat modStat) {
		modStats.remove(modStat);
		update(this);
	}

	@Override
	public void changeValue(double newValue) {
		base.changeValue(newValue);
	}

	@Override
	public void increaseValue(double amount) {
		base.increaseValue(amount);
	}

	@Override
	public void decreaseValue(double amount) {
		base.decreaseValue(amount);
	}

	@Override
	public String toString() {
		return "AuraStats{" +
				"modStats=" + modStats +
				", base=" + base +
				'}';
	}
}
