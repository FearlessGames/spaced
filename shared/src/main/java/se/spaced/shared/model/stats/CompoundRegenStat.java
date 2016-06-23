package se.spaced.shared.model.stats;

import se.fearless.common.stats.DerivedStat;
import se.fearless.common.stats.MutableStat;
import se.fearless.common.stats.Stat;

public class CompoundRegenStat extends DerivedStat implements MutableStat {

	private final MutableStat baseRegenRate;
	private final Stat outOfCombatRegen;

	public CompoundRegenStat(MutableStat baseRegenRate, Stat outOfCombatRegen, String statName) {
		super(statName, baseRegenRate, outOfCombatRegen);
		this.baseRegenRate = baseRegenRate;
		this.outOfCombatRegen = outOfCombatRegen;
		update(null);
	}

	@Override
	protected double recalculateValue() {
		return baseRegenRate.getValue() + outOfCombatRegen.getValue();
	}

	@Override
	public void changeValue(double newValue) {
		baseRegenRate.changeValue(newValue);
	}

	@Override
	public void increaseValue(double amount) {
		baseRegenRate.increaseValue(amount);
	}

	@Override
	public void decreaseValue(double amount) {
		baseRegenRate.decreaseValue(amount);
	}
}
