package se.spaced.shared.model.stats;

import org.junit.Test;
import se.fearless.common.stats.SimpleStat;

import static org.junit.Assert.assertEquals;

public class AttackModifierTest {

	private static final double EPSILON = 1e-10;

	@Test
	public void init() throws Exception {
		SimpleStat rating = new SimpleStat("Foo", 0);
		AttackModifier attackModifier = new AttackModifier(rating, EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER);

		assertEquals(1.0, attackModifier.getValue(), EPSILON);
	}

	@Test
	public void onePercentMore() throws Exception {
		SimpleStat rating = new SimpleStat("Foo", EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER);
		AttackModifier attackModifier = new AttackModifier(rating, EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER);

		assertEquals(1.01, attackModifier.getValue(), EPSILON);
	}

	@Test
	public void onePercentMoreCustomMultiplier() throws Exception {
		SimpleStat rating = new SimpleStat("Foo", 100);
		AttackModifier attackModifier = new AttackModifier(rating, 25);

		assertEquals(1.04, attackModifier.getValue(), EPSILON);
	}

	@Test
	public void changeRatingChangesMultiplier() throws Exception {
		SimpleStat rating = new SimpleStat("Foo", EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER);
		AttackModifier attackModifier = new AttackModifier(rating, EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER);

		rating.changeValue(EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER * 5);

		assertEquals(1.05, attackModifier.getValue(), EPSILON);
	}

	@Test
	public void onePercentLess() throws Exception {
		SimpleStat rating = new SimpleStat("Foo", -EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER);
		AttackModifier attackModifier = new AttackModifier(rating, EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER);

		assertEquals(0.99, attackModifier.getValue(), EPSILON);
	}


}
