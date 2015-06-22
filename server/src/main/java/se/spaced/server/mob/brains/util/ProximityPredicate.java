package se.spaced.server.mob.brains.util;

import com.google.common.base.Predicate;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.model.ServerEntity;

public class ProximityPredicate implements Predicate<ServerEntity> {

	private final ServerEntity self;
	private final double distanceSq;

	public ProximityPredicate(ServerEntity self, double distance) {
		this.self = self;
		distanceSq = distance * distance;
	}

	@Override
	public boolean apply(ServerEntity serverEntity) {
		return SpacedVector3.distanceSq(self.getPosition(), serverEntity.getPosition()) <= distanceSq;
	}
}
