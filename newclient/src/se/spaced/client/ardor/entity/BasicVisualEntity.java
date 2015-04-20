package se.spaced.client.ardor.entity;

import com.ardor3d.extension.animation.skeletal.SkeletonPose;
import com.ardor3d.extension.model.collada.jdom.data.SkinData;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.Ardor3dException;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.Rotations;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.animation.AnimationModel;
import se.spaced.client.model.animation.SpacedAttachment;
import se.spaced.client.tools.spd.ColorChangeVisitor;
import se.spaced.client.view.entity.VisualEntity;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.xmo.AttachmentPointIdentifier;
import se.spaced.shared.model.xmo.XmoAttachmentPoint;
import se.spaced.shared.model.xmo.XmoEntity;

import java.util.Map;

public class BasicVisualEntity implements VisualEntity {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Node entityNode;
	private final XmoEntity xmoEntity;
	private final AuraVisualiser auraVisualiser;
	private final ClientEntity entity;
	private AnimationModel animationModel;

	private boolean targeted;
	private boolean alive = true;
	private final InteractionColors interactionColors;
	private final Map<AttachmentPointIdentifier, SpacedAttachment> attachmentPoints = Maps.newHashMap();

	public BasicVisualEntity(
			InteractionColors interactionColors, Node entityNode,
			XmoEntity xmoEntity,
			AuraVisualiser auraVisualiser,
			ClientEntity entity) {
		this.interactionColors = interactionColors;
		this.entityNode = entityNode;
		this.xmoEntity = xmoEntity;
		this.auraVisualiser = auraVisualiser;

		this.entity = entity;
	}

	@Override
	public SpacedVector3 getMetaNodePosition(final String name) {
		return xmoEntity.getMetaNode(name).getPosition();
	}

	@Override
	public void setPositionData(SpacedVector3 position, SpacedRotation rotation) {
		try {
			entityNode.setRotation(Rotations.fromSpaced(rotation));
		} catch (Ardor3dException e) {
			log.error("Failed to set rotation on entity " + rotation, e);
		}
		entityNode.setTranslation(position);
	}

	@Override
	public void update(long t) {
		entityNode.updateGeometricState(0.0, true);

		if (animationModel != null) {
			animationModel.update();
		}
	}

	@Override
	public void setAlive(boolean aliveState) {
		alive = aliveState;
		updateColorState();
	}

	@Override
	public void setTargeted(boolean targetedState) {
		targeted = targetedState;
		updateColorState();
	}

	@Override
	public boolean removeFromParent() {
		return entityNode.removeFromParent();
	}

	@Override
	public boolean isActive() {
		return entityNode.getParent() != null;
	}

	private void updateColorState() {
		ColorRGBA color = new ColorRGBA(MaterialState.DEFAULT_EMISSIVE);
		if (!alive) {
			color.addLocal(interactionColors.getDeadColor());
		}

		if (targeted) {
			color.addLocal(interactionColors.getTargetColor());
		}

		setAmbientColor(color);
	}

	private void setAmbientColor(final ReadOnlyColorRGBA color) {
		final Spatial spatial = xmoEntity.getModel();
		spatial.acceptVisitor(new ColorChangeVisitor(color), false);
	}

	@Override
	public SpacedVector3 getSize() {
		return xmoEntity.getSize();
	}

	@Override
	public Node getNode() {
		return entityNode;
	}

	@Override
	public Node getModelNode() {
		return xmoEntity.getModel();
	}

	@Override
	public void setParent(final Node parentNode) {
		parentNode.attachChild(entityNode);
	}

	@Override
	public void play(AnimationState s) {
		updateJetpack(s);
		if (animationModel != null) {
			animationModel.play(s);
		}
	}

	private void updateJetpack(AnimationState s) {
		if (s == AnimationState.FLY) {
			auraVisualiser.fireEvent(AuraVisualiserEvent.JETPACK_STARTED, this);
		} else if (s == AnimationState.FLY_THRUST) {
			auraVisualiser.fireEvent(AuraVisualiserEvent.JETPACK_THRUST, this);
		} else {
			auraVisualiser.fireEvent(AuraVisualiserEvent.JETPACK_STOPPED, this);
		}
	}

	@Override
	public void equip(XmoEntity xmoEntity, AttachmentPointIdentifier where) {
		SpacedAttachment ap = attachmentPoints.get(where);
		if (ap != null) {
			ap.attachEntity(xmoEntity, this.getModelNode());
		}
	}

	@Override
	public void unequip(AttachmentPointIdentifier where) {
		SpacedAttachment ap = attachmentPoints.get(where);
		if (ap != null) {
			ap.removeAttachment();
		}
	}

	@Override
	public boolean hasEquipped(AttachmentPointIdentifier where) {
		SpacedAttachment ap = attachmentPoints.get(where);
		if (ap != null) {
			return ap.hasEquipped();
		}
		return false;
	}

	@Override
	public SkinData getSkin() {
		return xmoEntity.getSkin();
	}

	@Override
	public Transform getAttachmentJointLocalTransform(AttachmentPointIdentifier where, String jointName) {
		SpacedAttachment ap = attachmentPoints.get(where);
		if (ap != null) {
			return ap.getJointLocalTransform(jointName);
		}
		return null;
	}

	@Override
	public SkeletonPose getSkeletonPose() {
		return animationModel.getAnimationManager().getSkeletonPose(0);
	}

	@Override
	public void updatePoseTransforms(AttachmentPointIdentifier where) {
		SpacedAttachment ap = attachmentPoints.get(where);
		if (ap != null) {
			ap.updatePoseTransforms();
		}
	}

	public void setAnimationModel(AnimationModel animationModel) {
		this.animationModel = animationModel;
		entityNode.attachChild(animationModel.getNode());
	}

	public AnimationModel getAnimationModel() {
		return animationModel;
	}

	public void setupAttachmentPoints() {
		if (xmoEntity != null && xmoEntity.getXmoAttachmentPoints() != null) {
			Map<AttachmentPointIdentifier, XmoAttachmentPoint> xmoAttachmentPoints = xmoEntity.getXmoAttachmentPoints();
			for (AttachmentPointIdentifier xmoApId : xmoAttachmentPoints.keySet()) {
				XmoAttachmentPoint xmoAttachmentPoint = xmoAttachmentPoints.get(xmoApId);
				attachmentPoints.put(xmoApId, animationModel.createAttachmentPoint(xmoApId, xmoAttachmentPoint));
			}
		}
	}
}
