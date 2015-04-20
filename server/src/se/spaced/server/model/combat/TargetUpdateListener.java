package se.spaced.server.model.combat;

import se.spaced.server.model.ServerEntity;

public interface TargetUpdateListener {
	void targetCleared(ServerEntity entity, ServerEntity oldTarget);
	void targetChanged(ServerEntity entity, ServerEntity oldTarget, ServerEntity newTarget);
}
