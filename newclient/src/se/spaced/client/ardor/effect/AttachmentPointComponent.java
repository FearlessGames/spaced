package se.spaced.client.ardor.effect;

import com.ardor3d.extension.animation.skeletal.PoseListener;
import com.ardor3d.extension.animation.skeletal.SkeletonPose;
import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.view.entity.VisualEntity;

public class AttachmentPointComponent<T extends SpatialEffect> implements EffectComponent<T>, PoseListener {
	private static final Logger log = LoggerFactory.getLogger(AttachmentPointComponent.class);
	private final VisualEntity entity;
	private final String jointName;

	private ReadOnlyTransform store = Transform.IDENTITY;
	private int jointIndex;

	public AttachmentPointComponent(VisualEntity entity, String jointName) {
		this.entity = entity;
		this.jointName = jointName;
	}

	@Override
	public void onStart(T effect) {
		final SkeletonPose skeletonPose = entity.getSkeletonPose();

		if (skeletonPose == null) {
			log.warn("Missing data for {} when trying to start {}", entity, effect);
			return;
		}

		skeletonPose.addPoseListener(this);
		jointIndex = skeletonPose.getSkeleton().findJointByName(jointName);
		store = skeletonPose.getGlobalJointTransforms()[jointIndex];
		effect.setTransform(store);
	}

	@Override
	public void onStop(T effect) {
		final SkeletonPose skeletonPose = entity.getSkeletonPose();

		if (skeletonPose == null) {
			log.warn("Missing data for {} when trying to stop {}", entity, effect);
			return;
		}

		skeletonPose.removePoseListener(this);
	}

	@Override
	public void onUpdate(double dt, T effect) {
		if (entity.isActive()) {
			effect.setTransform(store);
		}
	}

	@Override
	public void poseUpdated(SkeletonPose skeletonPose) {
		store = skeletonPose.getGlobalJointTransforms()[jointIndex];
	}
}
