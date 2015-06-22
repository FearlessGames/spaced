package se.spaced.server.mob.brains;

import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spell.ServerSpell;

public class HealingBrain extends AbstractMobBrain {
	private final double minimumHealthPercentage;
	private final ServerSpell primaryHealingSpell;

	public HealingBrain(Mob mob, MobOrderExecutor orderExecutor, double minimumHealthPercentage, ServerSpell primaryHealingSpell) {
		super(mob, orderExecutor);
		this.minimumHealthPercentage = minimumHealthPercentage;
		this.primaryHealingSpell = primaryHealingSpell;
	}


	@Override
	public MobDecision act(long now) {
		if (mob.getBaseStats().getCurrentHealth().getValue() / mob.getBaseStats().getMaxHealth().getValue() < (minimumHealthPercentage / 100d)) {
			orderExecutor.castSpell(mob, mob, primaryHealingSpell);
			return MobDecision.DECIDED;
		}
		return MobDecision.UNDECIDED;
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return S2CEmptyReceiver.getSingleton();
	}

}