package se.spaced.server.model.aura;

import se.spaced.messages.protocol.AuraInstance;
import se.spaced.server.model.ServerEntity;

public interface AuraUpdateListener {
	void gainedAura(ServerEntity receiver, AuraInstance aura, ServerEntity performer);

	void lostAura(ServerEntity entity, AuraInstance aura);
}
