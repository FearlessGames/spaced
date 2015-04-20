package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.AuraInstance;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.CooldownData;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.model.MagicSchool;

@SmrtProtocol
public interface ServerCombatMessages {
	void combatStatusChanged(Entity entity, boolean isStart);

	void entityStartedSpellCast(Entity entity, Entity target, Spell spell);

	void entityCompletedSpellCast(Entity entity, Entity target, Spell spell);

	void entityStoppedSpellCast(Entity entity, Spell spell);

	void entityWasKilled(Entity attacker, Entity target);

	void entityDamaged(Entity from, Entity to, int amount, int newHealth, String source, MagicSchool school);

	void entityHealed(Entity from,  Entity to, int amount, int newHealth, String source, MagicSchool school);

	void entityHeatAffected(Entity from, Entity to, int amount, int newHeat, String source, MagicSchool school);

	void entityMissed(Entity from, Entity to, String source, MagicSchool school);

	void effectApplied(Entity from, Entity on, String resource);

	void gainedAura(Entity entity, AuraInstance aura);

	void lostAura(Entity entity, AuraInstance aura);

	void cooldownConsumed(Cooldown cooldown);
	
	void cooldownData(CooldownData cooldownData);

	void entityAbsorbedDamaged(
			Entity attacker,
			Entity target,
			int absorbedDamage,
			int value,
			String causeName, MagicSchool school);

	void entityRecovered(
			Entity performer,
			Entity target,
			int amount,
			int value,
			String causeName,
			MagicSchool school);
}
