package se.spaced.shared.model.xmo;

import com.ardor3d.extension.animation.skeletal.SkinnedMesh;
import com.ardor3d.extension.model.collada.jdom.data.SkinData;
import com.ardor3d.math.Transform;
import com.ardor3d.renderer.state.GLSLShaderObjectsState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.physics.PhysicsObject;

import java.util.List;
import java.util.Map;

public class XmoEntity {
	private static final Logger log = LoggerFactory.getLogger(XmoEntity.class);
	private Node model;
	private SkinData skin;
	private SpacedVector3 size;
	private final List<Mesh> physicsMeshes = Lists.newArrayList();
	private final List<PhysicsObject<?>> collisionObjects = Lists.newArrayList();
	private Map<String, MetaNode> metaNodes = Maps.newHashMap();
	private Map<AttachmentPointIdentifier, XmoAttachmentPoint> xmoAttachmentPoints = Maps.newHashMap();
	private String animationMappingKey;

	public XmoEntity(Node model, SpacedVector3 size) {
		this.model = model;
		this.size = size;
	}

	public MetaNode getMetaNode(final String name) {
		final MetaNode metaNode = metaNodes.get(name);
		if (metaNode != null) {
			return metaNode;
		}
		log.error("Tried to get non existing MetaNode named: " + name);
		return XmoMetaNode.NULL;
	}

	public void setMetaNodes(Map<String, MetaNode> metaNodes) {
		this.metaNodes = metaNodes;
	}

	public Node getModel() {
		return model;
	}

	public List<Mesh> getPhysicsMeshes() {
		return physicsMeshes;
	}

	public void setModel(Node model) {
		this.model = model;
	}

	public SpacedVector3 getSize() {
		return size;
	}

	public void setSize(SpacedVector3 size) {
		this.size = size;
	}

	public void addPhysicsMesh(Mesh physicsMesh) {
		physicsMeshes.add(physicsMesh);
	}

	public List<PhysicsObject<?>> getCollisionObjects() {
		return collisionObjects;
	}

	public SkinData getSkin() {
		return skin;
	}

	public void setSkin(SkinData skin) {
		this.skin = skin;
	}

	public void setXmoAttachmentPoints(Map<AttachmentPointIdentifier, XmoAttachmentPoint> xmoAttachmentPoints) {
		this.xmoAttachmentPoints = xmoAttachmentPoints;
	}

	public Map<AttachmentPointIdentifier, XmoAttachmentPoint> getXmoAttachmentPoints() {
		return xmoAttachmentPoints;
	}

	public void setSkinToSkinnedMeshesUnderNode(final SkinData skinData, Node node, final GLSLShaderObjectsState gpuShader) {
		node.acceptVisitor(new Visitor() {
			@Override
			public void visit(final Spatial spatial) {
				if (spatial instanceof SkinnedMesh) {
					SkinnedMesh skinnedMesh = (SkinnedMesh) spatial;
					if (skinData != null) {
						skinnedMesh.setCurrentPose(skinData.getPose());
					}
					skinData.getPose().addPoseListener(skinnedMesh);
					skinnedMesh.recreateWeightAttributeBuffer();
					skinnedMesh.recreateJointAttributeBuffer();

					skinnedMesh.setGPUShader(gpuShader);
					skinnedMesh.setUseGPU(gpuShader != null);
				}
			}
     	}, true);
	}

	public Transform getJointLocalTransform(String jointName) {
		if (skin != null) {
			int joint = skin.getPose().getSkeleton().findJointByName(jointName);
			if (joint >= 0) {
				return skin.getPose().getLocalJointTransforms()[joint];
			}
		}

		return null;
	}

	public void updatePoseTransforms() {
		if (skin != null) {
			skin.getPose().updateTransforms();
		}
	}

	public String getAnimationMappingKey() {
		return animationMappingKey;
	}

	public void setAnimationMappingKey(String animationMappingKey) {
		this.animationMappingKey = animationMappingKey;
	}
}