package se.spaced.server.model.cooldown;

import se.spaced.messages.protocol.s2c.ServerCombatMessages;
import se.spaced.shared.util.math.LinearTimeValue;

public class SimpleCooldown implements ServerCooldown {

	private final CooldownTemplate cooldownTemplate;
	private final LinearTimeValue linearTimeValue;

	public SimpleCooldown(long now, CooldownTemplate cooldownTemplate) {
		this.cooldownTemplate = cooldownTemplate;
		linearTimeValue = new LinearTimeValue(cooldownTemplate.getCooldownTime());

		// Start the cooldown fully charged
		linearTimeValue.setCurrentRate(now, 1.0);
		linearTimeValue.setValue(now, linearTimeValue.getMaxValue());
	}

	@Override
	public boolean isReady(long now) {
		return linearTimeValue.isReady(now, linearTimeValue.getMaxValue());
	}

	@Override
	public void consume(long now) {
		linearTimeValue.setValue(now, 0D);
	}

	@Override
	public void notifyPerformer(ServerCombatMessages receiver, long now) {
		receiver.cooldownConsumed(cooldownTemplate);
	}

	public CooldownTemplate getCooldownTemplate() {
		return cooldownTemplate;
	}

	public LinearTimeValue getLinearTimeValue() {
		return linearTimeValue;
	}
}
