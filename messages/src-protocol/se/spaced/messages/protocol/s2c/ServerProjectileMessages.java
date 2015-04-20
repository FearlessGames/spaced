package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;

@SmrtProtocol
public interface ServerProjectileMessages {
	void homingProjectileCreated(int projectileId, Entity performer, Entity target, String effectResource, double speed);

	void homingProjectileHit(int projectileId);
}