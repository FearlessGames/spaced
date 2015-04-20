package se.spaced.server.model.spell.effect;

import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

public class ProjectilEffectInstance {
	private final double speed;
	private final Iterable<Effect> impactEffects;
	private final ServerEntity performer;
	private final ServerEntity target;
	private SpacedVector3 position;
	private final String causeName;
	private final int projectileId;
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;

	public ProjectilEffectInstance(
			double speed,
			Iterable<Effect> impactEffects,
			ServerEntity performer,
			ServerEntity target,
			String causeName,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			int projectileId) {
		this.smrtBroadcaster = smrtBroadcaster;
		this.projectileId = projectileId;
		if (speed <= 0) {
			throw new IllegalArgumentException("Speed must be positive but was " + speed);
		}
		this.speed = speed;
		this.impactEffects = impactEffects;
		this.performer = performer;
		this.target = target;
		this.causeName = causeName;
		position = performer.getPosition();
	}


	public double move(double delta) {
		position = 	VectorMath.moveTowards(position, target.getPosition(), speed, delta);
		return getTargetDistance() / speed;
	}

	public double getTargetDistance() {
		return SpacedVector3.distance(target.getPosition(), position);
	}

	public void onImpact(long now) {
		smrtBroadcaster.create().toCombat(performer, target).send().projectile().homingProjectileHit(projectileId);
		for (Effect impactEffect : impactEffects) {
			impactEffect.apply(now, performer, target, causeName);
		}
	}

	public int getProjectileId() {
		return projectileId;
	}
}
