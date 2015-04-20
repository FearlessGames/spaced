package se.spaced.server.model.contribution;

import se.spaced.messages.protocol.AuraInstance;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.CooldownData;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.ServerCombatMessages;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.Combat;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.shared.model.MagicSchool;

public class ContributionMapper implements ServerCombatMessages {

	private final ContributionService contributionService;
	private final CombatRepository combatRepository;

	public ContributionMapper(ContributionService contributionService, CombatRepository combatRepository) {
		this.contributionService = contributionService;
		this.combatRepository = combatRepository;
	}

	@Override
	public void combatStatusChanged(Entity entity, boolean isStart) {
		contributionService.clearContributions((ServerEntity) entity);
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
	}

	@Override
	public void entityDamaged(Entity from, Entity to, int amount, int newHealth, String source, MagicSchool school) {
		contributionService.addContribution((ServerEntity) to, (ServerEntity) from, amount);
	}

	@Override
	public void entityHealed(Entity from, Entity to, int amount, int newHealth, String source, MagicSchool school) {
		handleFriendlyAction((ServerEntity) from, (ServerEntity) to, amount);
	}

	private void handleFriendlyAction(ServerEntity from, ServerEntity to, int amount) {
		Combat currentCombat = combatRepository.getCombat(to);
		if (currentCombat == null) {
			return;
		}
		for (ServerEntity serverEntity : currentCombat.getParticipants()) {
			if (contributionService.isContributor(serverEntity, to)) {
				contributionService.addContribution(serverEntity, from, amount);
			}
		}
	}

	@Override
	public void entityHeatAffected(Entity from, Entity to, int amount, int newHeat, String source, MagicSchool school) {
		if (amount > 0) {
			handleFriendlyAction((ServerEntity) from, (ServerEntity) to, amount);
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
	public void cooldownConsumed(Cooldown cooldown) {
	}

	@Override
	public void cooldownData(CooldownData cooldownData) {
	}

	@Override
	public void entityAbsorbedDamaged(Entity attacker, Entity target, int absorbedDamage, int value, String causeName, MagicSchool school) {
		contributionService.addContribution((ServerEntity) target, (ServerEntity) attacker, absorbedDamage);
	}

	@Override
	public void entityRecovered(Entity performer, Entity target, int amount, int value, String causeName, MagicSchool school) {
		handleFriendlyAction((ServerEntity) performer, (ServerEntity) target, amount);
	}
}
