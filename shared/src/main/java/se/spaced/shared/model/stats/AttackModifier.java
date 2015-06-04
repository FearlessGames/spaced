package se.spaced.shared.model.stats;

public class AttackModifier extends DerivedStat {

	private final Stat attackRating;
	private final double multiplier;

	public AttackModifier(Stat attackRating, double multiplier) {
		super("AttackModifier", attackRating);
		this.attackRating = attackRating;
		this.multiplier = (1 / multiplier) * 0.01;
		update(this);
	}

	@Override
	protected double recalculateValue() {
		return 1.0 + (attackRating.getValue() * multiplier);
	}
}