package se.spaced.client.model.animation;

import com.ardor3d.extension.animation.skeletal.AttachmentPoint;
import com.ardor3d.math.Transform;
import com.ardor3d.scenegraph.Node;
import se.spaced.shared.model.xmo.XmoEntity;

public class SpacedAttachment {
	private final AttachmentPoint attachmentPoint;
	private XmoEntity attachedEntity;

	public SpacedAttachment(AttachmentPoint attachmentPoint) {
		this.attachmentPoint = attachmentPoint;
	}

	public XmoEntity getAttachedEntity() {
		return attachedEntity;
	}

	public void attachEntity(XmoEntity attachedEntity, Node parentRenderNode) {
		this.attachedEntity = attachedEntity;
		attachmentPoint.setAttachment(attachedEntity.getModel());
		parentRenderNode.attachChild(attachedEntity.getModel());
	}

	public void removeAttachment() {
		attachedEntity = null;
		attachmentPoint.getAttachment().removeFromParent();
		attachmentPoint.setAttachment(null);
	}

	public boolean hasEquipped() {
		return attachmentPoint.getAttachment() != null;		
	}

	public Transform getJointLocalTransform(String jointName) {
		if (attachedEntity != null) {
			return attachedEntity.getJointLocalTransform(jointName);
		}
		return null;
	}

	public void updatePoseTransforms() {
		if (attachedEntity != null) {
			attachedEntity.updatePoseTransforms();
		}
	}
}
