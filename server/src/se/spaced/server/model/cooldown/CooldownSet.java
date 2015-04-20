package se.spaced.server.model.cooldown;

import se.spaced.messages.protocol.s2c.ServerCombatMessages;

import java.util.Collection;

public class CooldownSet implements ServerCooldown {
	private final Collection<ServerCooldown> cooldowns;

	public CooldownSet(Collection<ServerCooldown> cooldowns) {
		this.cooldowns = cooldowns;
	}

	@Override
	public boolean isReady(long now) {
		for (ServerCooldown cooldown : cooldowns) {
			if (!cooldown.isReady(now)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void consume(long now) {
		for (ServerCooldown cooldown : cooldowns) {
			cooldown.consume(now);
		}
	}

	@Override
	public void notifyPerformer(ServerCombatMessages receiver, long now) {
		for (ServerCooldown cooldown : cooldowns) {
			cooldown.notifyPerformer(receiver, now);
		}

	}
}
