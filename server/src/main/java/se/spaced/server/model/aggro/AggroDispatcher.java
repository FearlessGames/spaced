package se.spaced.server.model.aggro;

import se.spaced.messages.protocol.AuraInstance;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.CooldownData;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.ServerCombatMessages;
import se.spaced.server.model.ServerEntity;
import se.spaced.shared.model.MagicSchool;

public class AggroDispatcher implements ServerCombatMessages {
	private final ServerEntity self;
	private final AggroManager aggro;

	public AggroDispatcher(ServerEntity self, AggroManager aggro) {
		this.self = self;
		this.aggro = aggro;
	}

	@Override
	public void combatStatusChanged(Entity entity, boolean isStart) {
		if (!isStart) {
			aggro.clearAll();
		}
	}

	@Override
	public void entityStartedSpellCast(Entity entity, Entity target, Spell spell) {
	}

	@Override
	public void entityCompletedSpellCast(Entity entity, Entity target, Spell spell) {
	}

	@Override
	public void entityStoppedSpellCast(Entity entity, Spell spell) {
	}

	@Override
	public void entityWasKilled(Entity attacker, Entity target) {
		if (target.equals(self)) {
			aggro.clearAll();
		} else {
			aggro.clearHate((ServerEntity) target);
		}
	}

	@Override
	public void entityDamaged(Entity from, Entity to, int amount, int newHealth, String source, MagicSchool school) {
		if (to.equals(self)) {
			aggro.addHate((ServerEntity) from, amount);
		}
	}

	@Override
	public void entityHealed(Entity from, Entity to, int amount, int newHealth, String source, MagicSchool school) {
		if (aggro.isAggroWith((ServerEntity) to)) {
			aggro.addHate((ServerEntity) from, amount);
		}
	}

	@Override
	public void entityHeatAffected(Entity from, Entity to, int amount, int newHeat, String source, MagicSchool school) {
		if (aggro.isAggroWith((ServerEntity) to) && amount > 0) {
			aggro.addHate((ServerEntity) from, amount);
		}
	}

	@Override
	public void entityMissed(Entity from, Entity to, String source, MagicSchool school) {
	}

	@Override
	public void effectApplied(Entity from, Entity on, String resource) {
	}

	@Override
	public void gainedAura(Entity entity, AuraInstance aura) {
	}

	@Override
	public void lostAura(Entity entity, AuraInstance aura) {
	}

	@Override
	public void cooldownConsumed(Cooldown coolDown) {
	}

	@Override
	public void cooldownData(CooldownData cooldownData) {
	}

	@Override
	public void entityAbsorbedDamaged(
			Entity attacker, Entity target, int absorbedDamage, int value, String causeName, MagicSchool school) {
		if (target.equals(self)) {
			aggro.addHate((ServerEntity) attacker, absorbedDamage);
		}
	}

	@Override
	public void entityRecovered(
			Entity performer, Entity target, int amount, int value, String causeName, MagicSchool school) {
		if (aggro.isAggroWith((ServerEntity) target)) {
			aggro.addHate((ServerEntity) performer, amount);
		}
	}
}
