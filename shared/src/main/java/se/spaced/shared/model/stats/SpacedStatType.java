package se.spaced.shared.model.stats;

import se.fearless.common.stats.StatType;

public enum SpacedStatType implements StatType<SpacedStatType> {
	STAMINA,
	SHIELD_CHARGE,
	SHIELD_EFFICIENCY,
	COOL_RATE,
	SPEED,
	SHIELD_RECOVERY,
	ATTACK_RATING;

	@Override
	public String getName() {
		return name();
	}
}
