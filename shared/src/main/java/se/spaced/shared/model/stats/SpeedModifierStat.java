package se.spaced.shared.model.stats;

import se.fearless.common.stats.SimpleStat;

public class SpeedModifierStat extends SimpleStat {
	private static final String NAME = "Speed";

	public SpeedModifierStat(double value) {
		super(NAME, value, 0);
	}
}
