package se.spaced.server.mob.brains.templates;

import com.google.inject.Inject;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.HealingBrain;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.model.spell.ServerSpell;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class HealingBrainTemplate extends BrainTemplate {
	private final double minimumHealthPercentage;

	@ManyToOne(fetch = FetchType.EAGER)
	private final ServerSpell primaryHealingSpell;
	@Transient
	private final MobOrderExecutor orderExecutor;

	@Inject
	protected HealingBrainTemplate(MobOrderExecutor orderExecutor) {
		this(orderExecutor, 0, null);
	}

	public HealingBrainTemplate(MobOrderExecutor orderExecutor, double minHealthPercentage, ServerSpell healingSpell) {
		super(null, null);
		this.orderExecutor = orderExecutor;
		this.minimumHealthPercentage = minHealthPercentage;
		this.primaryHealingSpell = healingSpell;
	}

	public ServerSpell getPrimaryHealingSpell() {
		return primaryHealingSpell;
	}

	public double getMinimumHealthPercentage() {
		return minimumHealthPercentage;
	}

	@Override
	public MobBrain createBrain(Mob mob, SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		return new HealingBrain(mob, orderExecutor, minimumHealthPercentage, primaryHealingSpell);
	}
}