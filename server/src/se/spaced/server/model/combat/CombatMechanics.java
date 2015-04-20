package se.spaced.server.model.combat;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.RangeableEffect;
import se.spaced.shared.util.math.interval.IntervalInt;

public interface CombatMechanics {

	boolean isAllowedToAttack(ServerEntity performer, ServerEntity target);

	int getRandomInRange(IntervalInt range);

	boolean isAllowedToStartCast(ServerEntity performer, ServerEntity target, ServerSpell spell, CurrentActionService currentActionService, long now);

	boolean isAllowedToCompleteCast(ServerEntity performer, ServerEntity target, ServerSpell spell, CurrentActionService currentActionService);

	long getCastTime(ServerEntity performer, ServerSpell spell);

	int getDamage(ServerEntity attacker, ServerEntity target, RangeableEffect effect, double multiplier);
}
