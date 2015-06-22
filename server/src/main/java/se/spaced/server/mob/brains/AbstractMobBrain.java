package se.spaced.server.mob.brains;

import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.shared.model.EntityInteractionCapability;

import java.util.EnumSet;

public abstract class AbstractMobBrain implements MobBrain {
	protected final Mob mob;
	protected final MobOrderExecutor orderExecutor;

	protected AbstractMobBrain(Mob mob, MobOrderExecutor orderExecutor) {
		this.mob = mob;
		this.orderExecutor = orderExecutor;
	}

	@Override
	public abstract MobDecision act(long now);

	@Override
	public EnumSet<EntityInteractionCapability> getInteractionCapabilities() {
		return EnumSet.noneOf(EntityInteractionCapability.class);
	}

	@Override
	public final Mob getMob() {
		return mob;
	}
}
