package se.spaced.client.view.entity;

import com.ardor3d.extension.animation.skeletal.SkeletonPose;
import com.ardor3d.extension.model.collada.jdom.data.SkinData;
import com.ardor3d.math.Transform;
import com.ardor3d.scenegraph.Node;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.xmo.AttachmentPointIdentifier;
import se.spaced.shared.model.xmo.XmoEntity;

public class VisualEntities {
	// Suppresses default constructor, ensuring non-instantiability.
	private VisualEntities() {
	}

	public static final VisualEntity EMPTY_ENTITY = new EmptyEntity();

	// TODO: replace this with an AbstractVisualEntity when we have one
	private static class EmptyEntity implements VisualEntity {
		private static final Node EMPTY_NODE = new Node();
		private static final SpacedVector3 VECTOR = SpacedVector3.ZERO;

		@Override
		public void update(long t) {
		}

		@Override
		public void setAlive(boolean aliveState) {
		}

		@Override
		public void setTargeted(boolean targetedState) {
		}

		@Override
		public boolean removeFromParent() {
			return false;
		}

		@Override
		public Node getNode() {
			return EMPTY_NODE;
		}

		@Override
		public Node getModelNode() {
			return EMPTY_NODE;
		}

		@Override
		public void setParent(Node parentNode) {
		}

		@Override
		public SpacedVector3 getSize() {
			return VECTOR;
		}

		@Override
		public SpacedVector3 getMetaNodePosition(String name) {
			return VECTOR;
		}

		@Override
		public void play(AnimationState s) {

		}

		@Override
		public void equip(XmoEntity xmoEntity, AttachmentPointIdentifier where) {
		}

		@Override
		public void unequip(AttachmentPointIdentifier where) {
		}

		@Override
		public boolean hasEquipped(AttachmentPointIdentifier where) {
			return false;
		}

		@Override
		public SkinData getSkin() {
			return null;
		}

		@Override
		public Transform getAttachmentJointLocalTransform(AttachmentPointIdentifier where, String jointName) {
			return null;
		}

		@Override
		public void updatePoseTransforms(AttachmentPointIdentifier where) {
		}

		@Override
		public SkeletonPose getSkeletonPose() {
			return null;
		}

		@Override
		public void setPositionData(SpacedVector3 position, SpacedRotation rotation) {
		}

		@Override
		public boolean isActive() {
			return false;
		}
	}
}
