package se.spaced.client.model.animation;

import com.ardor3d.extension.animation.skeletal.AnimationManager;
import com.ardor3d.extension.animation.skeletal.AttachmentPoint;
import com.ardor3d.extension.animation.skeletal.SkeletonPose;
import com.ardor3d.extension.animation.skeletal.blendtree.ClipSource;
import com.ardor3d.extension.animation.skeletal.blendtree.SimpleAnimationApplier;
import com.ardor3d.extension.animation.skeletal.clip.AnimationClip;
import com.ardor3d.extension.animation.skeletal.state.AbstractFiniteState;
import com.ardor3d.extension.animation.skeletal.state.SteadyState;
import com.ardor3d.extension.model.collada.jdom.data.SkinData;
import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyQuaternion;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.Timer;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.xmo.AttachmentPointIdentifier;
import se.spaced.shared.model.xmo.XmoAttachmentPoint;

import java.util.Map;

public class AnimationModel {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final AnimationClipCache animationCache;
	private final Node rootNode;
	private final String animationMappingKey;
	private final SkeletonPose skeletonPose;
	private final Map<AnimationState, SteadyState> stateMap = Maps.newHashMap();
	private final Timer timer = new Timer();
	private AnimationManager animationManager;

	private AbstractFiniteState currentState;

	public AnimationModel(
			AnimationClipCache animationCache,
			Node model,
			SkinData skinData,
			String animationMappingKey,
			AnimationState state) {
		this.animationCache = animationCache;
		rootNode = model;
		this.animationMappingKey = animationMappingKey;
		skeletonPose = skinData.getPose();

		// Always setup the manager
		setupManager(state);
	}


	private void setupManager(AnimationState state) {
		animationManager = new AnimationManager(timer, skeletonPose);
		animationManager.setApplier(new SimpleAnimationApplier());
		play(state);
	}

	public AnimationManager getAnimationManager() {
		return animationManager;
	}

	public void play(AnimationState stateName) {
		SteadyState newState = stateMap.get(stateName);
		if (newState == null) {
			newState = addState(stateName);
		}

		// Restart the new animation
		newState.resetClips(animationManager);

		final SpacedBlendState blender = new SpacedBlendState(newState.getName(), 0.17);
		blender.blendBetween(currentState, newState, animationManager.getBaseAnimationLayer());
		currentState = newState;
	}

	public void update() {
		try {
			animationManager.update();
			skeletonPose.updateTransforms();
		} catch (RuntimeException e) {
			log.error("Ardor animation fail: ", e);
		}
	}

	public Node getNode() {
		return rootNode;
	}

	private SteadyState addState(AnimationState stateName) {
		SteadyState state = new SteadyState(stateName.name());
		AnimationClip clip = animationCache.getClip(stateName, animationMappingKey);
		animationManager.getClipInstance(clip).setLoopCount(Integer.MAX_VALUE);
		animationManager.getBaseAnimationLayer().addSteadyState(state);
		state.setSourceTree(new ClipSource(clip, animationManager));

		stateMap.put(stateName, state);

		return state;
	}

	private AttachmentPoint createAttachmentPoint(
			AttachmentPointIdentifier attachmentPointIdentifier, String jointName,
			ReadOnlyVector3 offset, ReadOnlyVector3 scale, ReadOnlyQuaternion rotation) {

		final AttachmentPoint ap = new AttachmentPoint(attachmentPointIdentifier.name());
		final int jointIndex = skeletonPose.getSkeleton().findJointByName(jointName);
		ap.setJointIndex((short) jointIndex);
		skeletonPose.addPoseListener(ap);

		if (offset != null) {
			final Transform transform = new Transform();
			transform.setRotation(rotation);
			transform.setTranslation(offset);
			transform.setScale(scale);
			ap.setOffset(transform);
		} else {
			ap.setOffset(Transform.IDENTITY);
		}

		return ap;
	}

	public SpacedAttachment createAttachmentPoint(
			AttachmentPointIdentifier attachmentPointIdentifier,
			XmoAttachmentPoint attachmentPoint) {

		return new SpacedAttachment(createAttachmentPoint(
				attachmentPointIdentifier,
				attachmentPoint.getJointName(),
				attachmentPoint.getOffset(),
				attachmentPoint.getScale(),
				attachmentPoint.getRotation()));
	}
}
