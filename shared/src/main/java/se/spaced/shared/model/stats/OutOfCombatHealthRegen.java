package se.spaced.shared.model.stats;

import se.fearless.common.stats.DerivedStat;
import se.fearless.common.stats.Stat;

public class OutOfCombatHealthRegen extends DerivedStat {
	private final double secondsToFullRegen;
	private final Stat maxHealth;
	private double combatMod;

	public OutOfCombatHealthRegen(Stat maxHealth, double secondsToFullRegen) {
		super("oocHealthRegen", maxHealth);
		this.maxHealth = maxHealth;
		combatMod = 1.0;
		this.secondsToFullRegen = secondsToFullRegen;
		update(null);
	}

	@Override
	protected final double recalculateValue() {
		return maxHealth.getValue() * combatMod / secondsToFullRegen;
	}

	public void disable() {
		combatMod = 0.0;
		update(null);
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			enable();
		} else {
			disable();
		}
	}

	public void enable() {
		combatMod = 1.0;
		update(null);
	}

	public boolean isEnabled() {
		return Double.compare(combatMod, 1.0) == 0;
	}
}
