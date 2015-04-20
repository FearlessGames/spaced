package se.spaced.server.model.aura;

import se.spaced.server.model.ServerEntity;

public interface AuraInstanceRemover {
	void remove(ServerEntity target, ServerAuraInstance instance);
}
