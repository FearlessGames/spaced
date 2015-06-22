package se.spaced.server.model.combat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.action.OrderedAction;
import se.spaced.server.model.action.SpellAction;
import se.spaced.server.model.action.SpellListener;
import se.spaced.server.model.cooldown.ServerCooldown;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.RangeableEffect;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.model.TargetingType;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.ShieldStrength;

@Singleton
public class SpellCombatServiceImpl implements SpellCombatService {
	private final Logger logger = LoggerFactory.getLogger(SpellCombatServiceImpl.class);
	private final EntityCombatService entityCombatService;
	private final CombatMechanics combatMechanics;
	private final ActionScheduler scheduler;
	private final CurrentActionService currentActionService;
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private final DeathService deathService;

	@Inject
	public SpellCombatServiceImpl(
			EntityCombatService entityCombatService, CombatMechanics combatMechanics,
			ActionScheduler scheduler,
			CurrentActionService currentActionService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			DeathService deathService) {
		this.entityCombatService = entityCombatService;
		this.combatMechanics = combatMechanics;
		this.scheduler = scheduler;
		this.currentActionService = currentActionService;
		this.smrtBroadcaster = smrtBroadcaster;
		this.deathService = deathService;
	}

	@Override
	public void doDamage(ServerEntity attacker, ServerEntity target, long now, RangeableEffect origin, String causeName) {
		if (target.isAlive()) {
			EntityStats attackerBaseStats = attacker.getBaseStats();
			EntityStats targetBaseStats = target.getBaseStats();

			int damage = combatMechanics.getDamage(attacker, target, origin, attackerBaseStats.getAttackModifier().getValue());
			Combat combat = entityCombatService.enterCombat(attacker, target, now, true);
			ShieldStrength shieldStrength = targetBaseStats.getShieldStrength();
			double beforeAbsorb = shieldStrength.getValue();
			shieldStrength.decreaseValue(damage);
			double afterAbsorb = shieldStrength.getValue();
			int absorbedDamage = (int) (beforeAbsorb - afterAbsorb);
			if (absorbedDamage != 0) {
				smrtBroadcaster.create().toCombat(combat).send().combat().entityAbsorbedDamaged(attacker, target, absorbedDamage,
						(int) shieldStrength.getValue(), causeName, origin.getSchool());
			}
			int remainingDamage = damage - absorbedDamage;
			if (remainingDamage != 0) {
				targetBaseStats.getCurrentHealth().decreaseValue(remainingDamage);
				smrtBroadcaster.create().toCombat(combat).send().combat().entityDamaged(attacker, target, remainingDamage,
						(int) targetBaseStats.getCurrentHealth().getValue(), causeName, origin.getSchool());
			}
			if (targetBaseStats.getCurrentHealth().getValue() <= 0) {
				deathService.kill(target);
				smrtBroadcaster.create().toParty(attacker).toParty(target).toArea(target).send().combat().entityWasKilled(attacker, target);
				entityCombatService.removeFromCombat(target);
			}
		}
	}

	@Override
	public void doMiss(ServerEntity attacker, ServerEntity target, long now, RangeableEffect origin, String causeName) {
		if (target.isAlive()) {
			Combat combat = entityCombatService.enterCombat(attacker, target, now, true);
			smrtBroadcaster.create().toCombat(combat).send().combat().entityMissed(attacker, target, causeName, origin.getSchool());
		}
	}

	@Override
	public void doHeal(ServerEntity healer, ServerEntity target, long now, RangeableEffect origin, String causeName) {
		if (target.isAlive()) {
			int amount = combatMechanics.getDamage(healer, target, origin, 1.0);
			if (entityCombatService.isInCombat(target)) {
				entityCombatService.enterCombat(healer, target, now, false);
			}
			target.getBaseStats().getCurrentHealth().increaseValue(amount);
			smrtBroadcaster.create().toCombat(healer, target).send().combat().entityHealed(healer,
					target,
					amount,
					(int) target.getBaseStats().getCurrentHealth().getValue(),
					causeName,
					origin.getSchool());
		}
	}

	@Override
	public void doCool(ServerEntity performer, ServerEntity target, long now, RangeableEffect origin, String causeName) {
		if (target.isAlive()) {
			int amount = combatMechanics.getDamage(performer, target, origin, 1.0);
			if (entityCombatService.isInCombat(target)) {
				entityCombatService.enterCombat(performer, target, now, false);
			}
			target.getBaseStats().getHeat().generate(-amount);
			smrtBroadcaster.create().toCombat(performer, target).send().combat().entityHeatAffected(
					performer,
					target,
					amount,
					(int) target.getBaseStats().getHeat().getValue(),
					causeName,
					origin.getSchool());
		}
	}

	@Override
	public void doRecover(
			ServerEntity performer, ServerEntity target, long now, RangeableEffect recoverEffect, String causeName) {
		if (target.isAlive()) {
			int amount = combatMechanics.getDamage(performer, target, recoverEffect, 1.0);
			double shieldBefore = target.getBaseStats().getShieldStrength().getValue();
			if (entityCombatService.isInCombat(target)) {
				entityCombatService.enterCombat(performer, target, now, false);
			}
			target.getBaseStats().getShieldStrength().increaseValue(amount);
			double shieldAfter = target.getBaseStats().getShieldStrength().getValue();
			int recoveredAmount = (int) (shieldAfter - shieldBefore);
			smrtBroadcaster.create().toCombat(performer, target).to(performer, target).toArea(target
			).toParty(performer).toParty(target).send().combat().entityRecovered(
					performer,
					target,
					recoveredAmount,
					(int) target.getBaseStats().getShieldStrength().getValue(),
					causeName,
					recoverEffect.getSchool());
		}
	}


	@Override
	public void startSpellCast(ServerEntity performer, ServerEntity target, ServerSpell spell, long now, SpellListener listener) {
		TargetingType targetingType = spell.getTargetingType();
		if (targetingType == TargetingType.SELF_ONLY) {
			target = performer;
		}
		if (combatMechanics.isAllowedToStartCast(performer, target, spell, currentActionService, now)) {

			final ServerCooldown cooldown = performer.getCooldown(spell.getCoolDown(), now);
			cooldown.consume(now);
			cooldown.notifyPerformer(smrtBroadcaster.create().to(performer).send().combat(), now);

			long castTime = combatMechanics.getCastTime(performer, spell);
			OrderedAction action = createSpellAction(performer, target, now + castTime, spell, listener);
			currentActionService.setCurrentAction(action);
			scheduler.add(action);
			smrtBroadcaster.create().to(performer, target).toArea(performer).send().combat().entityStartedSpellCast(performer, target, spell);
		}
	}


	@Override
	public void stopSpellCast(ServerEntity entity) {
		currentActionService.cancelCurrentAction(entity);
	}

	@Override
	public void interruptSpellCast(ServerEntity entity) {
		// TODO: should this really be the same as stopSpellCast?
		currentActionService.cancelCurrentAction(entity);
	}

	private OrderedAction createSpellAction(ServerEntity performer, ServerEntity target, long executionTime, ServerSpell spell, SpellListener listener) {
		return new SpellAction(combatMechanics, executionTime, performer, target, spell, currentActionService, smrtBroadcaster, listener);
	}
}
