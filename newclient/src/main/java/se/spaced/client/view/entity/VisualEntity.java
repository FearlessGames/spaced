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

public interface VisualEntity {
	void update(long t);

	void setAlive(boolean aliveState);

	void setTargeted(boolean targetedState);

	boolean removeFromParent();

	// TODO: replace return type with something semantically more correct?
	SpacedVector3 getSize();

	Node getNode();

	Node getModelNode();

	void setParent(Node parentNode);

	SpacedVector3 getMetaNodePosition(String name);

	void play(AnimationState s);

	void equip(XmoEntity xmoEntity, AttachmentPointIdentifier where);

	void unequip(AttachmentPointIdentifier where);

	boolean hasEquipped(AttachmentPointIdentifier where);

	SkinData getSkin();

	Transform getAttachmentJointLocalTransform(AttachmentPointIdentifier where, String jointName);

	void updatePoseTransforms(AttachmentPointIdentifier where);

	SkeletonPose getSkeletonPose();

	void setPositionData(SpacedVector3 position, SpacedRotation rotation);

	boolean isActive();
}
