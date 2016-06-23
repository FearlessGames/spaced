package se.spaced.server.model.combat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.stats.Stat;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.OrderedAction;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.cooldown.CooldownSetTemplate;
import se.spaced.server.model.cooldown.ServerCooldown;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.RangeableEffect;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.util.random.RandomProvider;

@Singleton
public class SimpleCombatMechanics implements CombatMechanics {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final RandomProvider random;
	private final AuraService auraService;

	@Inject
	public SimpleCombatMechanics(RandomProvider randomProvider, AuraService auraService) {
		random = randomProvider;
		this.auraService = auraService;
	}

	@Override
	public int getDamage(ServerEntity attacker, ServerEntity target, RangeableEffect effect, double multiplier) {
		return (int) (getRandomInRange(effect.getRange()) * multiplier);
	}

	@Override
	public boolean isAllowedToAttack(ServerEntity performer, ServerEntity target) {
		if (performer == null || target == null) {
			return false;
		}
		if (performer.equals(target)) {
			return false;
		}
		return performer.isAlive() && target.isAlive();
	}

	@Override
	public int getRandomInRange(IntervalInt range) {
		return random.getInteger(range);
	}

	@Override
	public boolean isAllowedToStartCast(ServerEntity performer, ServerEntity target, ServerSpell spell, CurrentActionService currentActionService, long now) {
		if (target == null) {
			log.debug("Not allowed to start: No target for {}", performer);
			return false;
		}
		if (isOverheated(performer, spell)) {
			log.debug("Not allowed to start: Overheated {}", performer);
			return false;
		}
		if (spell.requiresHostileTarget() && !isAllowedToAttack(performer, target)) {
			log.debug("Not allowed to start: Requires hostile target and not allowed to attack {} - {}", performer, target);
			return false;
		}
		if (!hasRequiredAuras(performer, spell)) {
			log.debug("Not allowed to start: Does not have required auras {} - {}", performer, spell);
			return false;
		}
		// TODO: change this once we need ress
		if (!target.isAlive()) {
			return false;
		}

		final CooldownSetTemplate cooldownTemplate = spell.getCoolDown();
		final ServerCooldown cooldown = performer.getCooldown(cooldownTemplate, now);
		if (!cooldown.isReady(now)) {
			log.debug("Not allowed to start: CD {} - {}", performer, cooldown);
			return false;
		}
		//noinspection NumericCastThatLosesPrecision
		OrderedAction currentAction = currentActionService.getCurrentAction(performer);
		double distanceToTarget = SpacedVector3.distance(performer.getPosition(), target.getPosition());
		if (currentAction != null) {
			log.debug("Not allowed to start: Action in progress {} - {}", performer, currentAction);
			return false;
		}
		if (!spell.getRanges().contains((int) distanceToTarget)) {
			log.debug("Not allowed to start: Not in range {} - {}", performer, distanceToTarget);
			return false;
		}
		return true;
	}

	private boolean hasRequiredAuras(ServerEntity performer, ServerSpell spell) {
		for (ServerAura serverAura : spell.getRequiredAuras()) {
			if (!auraService.hasAura(performer, serverAura)) {
				return false;
			}
		}
		return true;
	}

	private boolean isOverheated(ServerEntity performer, ServerSpell spell) {
		Stat heat = performer.getHeat();
		double currentHeat = heat.getValue();
		log.debug("Heat is {}", currentHeat);
		double maxHeat = performer.getBaseStats().getMaxHeat().getValue();
		if (currentHeat >= (maxHeat - spell.getHeatContribution())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isAllowedToCompleteCast(ServerEntity performer, ServerEntity target, ServerSpell spell, CurrentActionService currentActionService) {
		if (spell.requiresHostileTarget() && !isAllowedToAttack(performer, target)) {
			return false;
		}
		// TODO: change this once we need ress
		if (!target.isAlive()) {
			return false;
		}
		if (!hasRequiredAuras(performer, spell)) {
			return false;
		}
		//noinspection NumericCastThatLosesPrecision
		return spell.getRanges().contains((int) SpacedVector3.distance(performer.getPosition(), target.getPosition()));
	}

	@Override
	public long getCastTime(ServerEntity performer, ServerSpell spell) {
		return spell.getCastTime();
	}
}