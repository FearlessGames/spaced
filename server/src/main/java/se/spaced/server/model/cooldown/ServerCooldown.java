package se.spaced.server.model.cooldown;

import se.spaced.messages.protocol.s2c.ServerCombatMessages;

public interface ServerCooldown {
	boolean isReady(long now);

	void consume(long now);

	void notifyPerformer(ServerCombatMessages receiver, long now);
}
