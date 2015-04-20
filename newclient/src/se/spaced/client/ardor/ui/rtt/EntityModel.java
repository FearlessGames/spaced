package se.spaced.client.ardor.ui.rtt;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardorgui.components.rtt.Rtt;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.ardor.entity.AuraVisualiser;
import se.spaced.client.ardor.entity.BasicVisualEntity;
import se.spaced.client.model.animation.AnimationClipCache;
import se.spaced.client.model.animation.AnimationModel;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.xmo.AttachmentPointIdentifier;
import se.spaced.shared.model.xmo.XmoEntity;
import se.spaced.shared.model.xmo.XmoEntityFactory;
import se.spaced.shared.xml.XmlIOException;

public class EntityModel {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final XmoEntityFactory xmoEntityFactory;
	private final AnimationClipCache animationCache;
	private final AuraVisualiser auraVisualiser;
	private final Rtt component;
	private final Quaternion quaternion = new Quaternion();
	private final Vector3 yAxis = new Vector3(0, 1, 0);
	private final Vector3 xAxis = new Vector3(1, 0, 0);
	private final Vector3 zAxis = new Vector3(0, 0, 1);
	private BasicVisualEntity visualEntity;

	public EntityModel(
			final Rtt component,
			final XmoEntityFactory xmoEntityFactory,
			AnimationClipCache animationCache, AuraVisualiser auraVisualiser) {
		this.component = component;
		this.xmoEntityFactory = xmoEntityFactory;
		this.animationCache = animationCache;
		this.auraVisualiser = auraVisualiser;
	}

	@LuaMethod(name = "Load")
	public void load(final String xmoFile) {
		try {	  //TODO: Should probably cache this
			XmoEntity xmoEntity = xmoEntityFactory.create(xmoFile, "");
			Node entityNode = new Node(xmoFile);
			BasicVisualEntity visualEntity = new BasicVisualEntity(
					null,
					entityNode,
					xmoEntity,
					auraVisualiser,
					null);
			if (xmoEntity.getSkin() != null) {
				visualEntity.setAnimationModel(new AnimationModel(animationCache, xmoEntity.getModel(), xmoEntity.getSkin(),
						xmoEntity.getAnimationMappingKey(), AnimationState.IDLE));
				visualEntity.setupAttachmentPoints();
			} else {
				entityNode.attachChild(xmoEntity.getModel());
			}

			this.visualEntity = visualEntity;
			component.setNodeToRender(visualEntity.getModelNode());
			component.updateTexture();
		} catch (XmlIOException e) {
			throw new RuntimeException(e);
		}
	}

	@LuaMethod(name = "PlayAnimation")
	public void playAnimation(AnimationState animationState) {
		visualEntity.play(animationState);
	}

	@LuaMethod(name = "Update")
	public void update() {
		if (visualEntity != null) {
			visualEntity.getAnimationModel().update();
			component.getNode().updateGeometricState(0, true);
		}
		component.updateTexture();

	}

	@LuaMethod(name = "Equip")
	public void equip(String xmoFile, ContainerType containerType) {

		AttachmentPointIdentifier where = containerType.getAttachmentPoint();
		if (where == AttachmentPointIdentifier.VEHICLE) {
			return;
		}

		try {
			XmoEntity xmoEntity = xmoEntityFactory.create(xmoFile, where.name());
			xmoEntity.setSkinToSkinnedMeshesUnderNode(visualEntity.getSkin(),
					xmoEntity.getModel(),
					xmoEntityFactory.getSkinningShader());
			if (visualEntity.hasEquipped(where)) {
				visualEntity.unequip(where);
			}
			visualEntity.equip(xmoEntity, where);
		} catch (XmlIOException e) {
			log.warn("Could not load requested equpiment xmo {}. {}", xmoFile, e.getCause());
		}
	}

	@LuaMethod(name = "Unequip")
	public void unequip(ContainerType containerType) {
		final AttachmentPointIdentifier point = containerType.getAttachmentPoint();
		if (visualEntity.hasEquipped(point)) {
			visualEntity.unequip(point);
		}
	}

	@LuaMethod(name = "SetModelOffset")
	public void setModelOffset(int x, int y) {
		component.setRttOffset(x, y);
	}

	@LuaMethod(name = "Rotate")
	public void rotate(
			final double a, final double b, final double c, double d, double e, double f, double g, double h, double i) {
		component.getNode().setRotation(new Matrix3(a, b, c, d, e, f, g, h, i));
		component.updateTexture();
	}

	@LuaMethod(name = "RotateYAxis")
	public void rotateYAxis(final double degrees) {
		rotateAxis(degrees, yAxis);
	}

	@LuaMethod(name = "RotateXAxis")
	public void rotateXAxis(final double degrees) {
		rotateAxis(degrees, xAxis);
	}

	@LuaMethod(name = "RotateZAxis")
	public void rotateZAxis(final double degrees) {
		rotateAxis(degrees, zAxis);
	}

	public void rotateAxis(final double degrees, final Vector3 axis) {
		quaternion.fromAngleNormalAxis(degrees, axis);
		component.getNode().setRotation(quaternion);
		component.updateTexture();
	}

}