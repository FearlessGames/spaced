package se.spaced.shared.model.stats;

import se.fearless.common.stats.AbstractStat;
import se.fearless.common.stats.DerivedStat;
import se.fearless.common.stats.Stat;

public class MaxHitPoints extends DerivedStat {
	private final Stat stamina;
	private final double factor;

	public MaxHitPoints(AbstractStat stamina, double factor) {
		super("MaxHealth", stamina);
		this.stamina = stamina;
		this.factor = factor;
		update(this);
	}

	@Override
	public double recalculateValue() {
		return stamina.getValue() * factor;
	}
}
