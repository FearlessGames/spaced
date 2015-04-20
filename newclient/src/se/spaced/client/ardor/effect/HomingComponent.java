package se.spaced.client.ardor.effect;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.view.entity.VisualEntity;

public class HomingComponent<T extends SpatialEffect> extends EffectComponentAdapter<T> {
	private final VisualEntity source;
	private final VisualEntity target;
	private final ReadOnlyVector3 sourceOffset;
	private final ReadOnlyVector3 targetOffset;
	private final double speed;
	private final Vector3 direction = new Vector3();
	private final Vector3 position = new Vector3();
	private final Vector3 targetPosition = new Vector3();

	public HomingComponent(
			VisualEntity source,
			SpacedVector3 sourceOffset,
			VisualEntity target,
			SpacedVector3 targetOffset,
			double speed) {
		this.source = source;
		this.sourceOffset = sourceOffset;
		this.target = target;
		this.targetOffset = targetOffset;
		this.speed = speed;
	}

	@Override
	public void onStart(final T effect) {
		source.getNode().getWorldTransform().applyForward(sourceOffset, position);
		effect.setTranslation(position);
		effect.setRotation(source.getNode().getWorldRotation());
	}

	@Override
	public void onUpdate(final double dt, final T effect) {
		final double step = speed * dt;
		target.getNode().getWorldTransform().applyForward(targetOffset, targetPosition);

		if (targetPosition.distanceSquared(position) < step * step) {
			effect.stop();
			return;
		}

		targetPosition.subtract(position, direction);
		direction.normalizeLocal();

		direction.multiplyLocal(step);
		position.addLocal(direction);
		effect.setTranslation(position);
		effect.setVelocity(direction);
	}
}
