package se.spaced.server.model.combat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

@Singleton
public class TargetUpdateBroadcaster implements TargetUpdateListener {
	private final SmrtBroadcaster<S2CProtocol> broadcaster;

	@Inject
	public TargetUpdateBroadcaster(SmrtBroadcaster<S2CProtocol> broadcaster) {
		this.broadcaster = broadcaster;
	}

	@Override
	public void targetCleared(ServerEntity entity, ServerEntity oldTarget) {
		broadcaster.create().to(entity).send().entity().clearedTarget();
		broadcaster.create().toArea(entity).exclude(entity).send().entity().entityClearedTarget(entity);
	}

	@Override
	public void targetChanged(ServerEntity entity, ServerEntity oldTarget, ServerEntity newTarget) {
		broadcaster.create().to(entity).send().entity().changedTarget(newTarget);
		broadcaster.create().toArea(entity).exclude(entity).send().entity().entityChangedTarget(entity, newTarget);
	}
}
