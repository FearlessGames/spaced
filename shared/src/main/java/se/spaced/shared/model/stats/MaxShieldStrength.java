package se.spaced.shared.model.stats;

import se.fearless.common.stats.AbstractStat;
import se.fearless.common.stats.DerivedStat;

public class MaxShieldStrength extends DerivedStat {
	private final AbstractStat shieldEfficiency;
	private final AbstractStat shieldCharge;

	public MaxShieldStrength(AbstractStat shieldEfficiency, AbstractStat shieldCharge) {
		super("MaxShieldStrength", shieldEfficiency, shieldCharge);
		this.shieldEfficiency = shieldEfficiency;
		this.shieldCharge = shieldCharge;
		update(this);
	}

	@Override
	protected double recalculateValue() {
		return shieldEfficiency.getValue() * shieldCharge.getValue();
	}
}
