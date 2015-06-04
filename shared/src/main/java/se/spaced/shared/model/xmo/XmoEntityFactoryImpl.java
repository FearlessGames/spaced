package se.spaced.shared.model.xmo;

import com.ardor3d.extension.animation.skeletal.SkeletonPose;
import com.ardor3d.extension.model.collada.jdom.data.SkinData;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.GLSLShaderObjectsState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.TextureManager;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.shared.resources.XmoMaterialManager;
import se.spaced.shared.xml.XmlIOException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Singleton
public class XmoEntityFactoryImpl implements XmoEntityFactory {

	private final XmoMaterialManager xmoMaterialManager;

	private final TextureManager xmoTextureManager;

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ColladaContentLoader colladaContentLoader;
	private final StreamLocator streamLocator;
	private GLSLShaderObjectsState skinningShader;
	private final XmoLoader xmoLoader;

	@Inject
	public XmoEntityFactoryImpl(
			ColladaContentLoader colladaContentLoader,
			XmoMaterialManager xmoMaterialManager,
			TextureManager xmoTextureManager,
			StreamLocator streamLocator,
			XmoLoader xmoLoader) {
		this.xmoMaterialManager = xmoMaterialManager;
		this.xmoTextureManager = xmoTextureManager;
		this.streamLocator = streamLocator;
		this.colladaContentLoader = colladaContentLoader;
		this.xmoLoader = xmoLoader;

		loadSkinningShader();
	}

	@Override
	public XmoEntity create(String xmoFile, final String entityName) throws XmlIOException {
		XmoRoot xmoRoot = xmoLoader.loadXmo(xmoFile);
		final Node rootNode = new Node(xmoRoot.getName());
		final XmoEntity xmoEntity = new XmoEntity(rootNode, xmoRoot.getSize());


		setDefaultMaterialForNode(rootNode);
		if (xmoRoot.getExtendedMeshObjects() != null) {
			addExtendedMeshObjectsToNode(xmoRoot.getExtendedMeshObjects(), rootNode, entityName, xmoEntity);
		}

		if (xmoRoot.getContainerNodes() != null) {
			addContainerNodesToNode(xmoRoot.getContainerNodes(), rootNode, entityName, xmoEntity);
		}

		xmoEntity.setModel(rootNode);

		rootNode.updateWorldBound(true);

		final Map<String, MetaNode> metaNodes = xmoRoot.getMetaNodes();
		if (metaNodes != null) {
			xmoEntity.setMetaNodes(metaNodes);
		}
		Map<AttachmentPointIdentifier, XmoAttachmentPoint> xmoAttachmentPoints = xmoRoot.getXmoAttachmentPoints();
		if (xmoAttachmentPoints != null) {
			xmoEntity.setXmoAttachmentPoints(xmoAttachmentPoints);
		}

		if (xmoRoot.getAnimationData() != null) {
			addAnimationData(xmoRoot, xmoEntity, rootNode);
		}
		return xmoEntity;
	}

	private void addAnimationData(XmoRoot xmoRoot, XmoEntity entity, Node parentNode) {
		AnimationData animationData = xmoRoot.getAnimationData();
		ColladaContents colladaContents = colladaContentLoader.get("/" + animationData.getSkeletonFile());
		entity.setAnimationMappingKey(animationData.getAnimationMapping());
//		Node node = colladaContents.scene.makeCopy(false);

		SkinData skin = cloneSkinData(colladaContents.getSkin(), parentNode);
		entity.setSkin(skin);

		entity.setSkinToSkinnedMeshesUnderNode(entity.getSkin(), parentNode, getSkinningShader());

		// TODO: Should a skeleton really have these parameters?
//		parentNode.setScale(animationData.getScale());
//		parentNode.setTranslation(animationData.getPosition());
//		parentNode.setRotation(animationData.getRotation());
	}


	private void loadSkinningShader() {
		skinningShader = new GLSLShaderObjectsState();
		skinningShader.setEnabled(true);
		try {

			skinningShader.setVertexShader(streamLocator.getInputSupplier("shaders/skinning_gpu.vert").getInput());
			skinningShader.setFragmentShader(streamLocator.getInputSupplier("shaders/simpleTextureShader.frag").getInput());
		} catch (final IOException ioe) {
			log.error("Failed to load skinning shader");
			log.error(ioe.toString());
		}
	}

	@Override
	public GLSLShaderObjectsState getSkinningShader() {
		return skinningShader;
	}

	private void setDefaultMaterialForNode(Node node) {
		MaterialState ms = new MaterialState();
		ms.setColorMaterial(MaterialState.ColorMaterial.Diffuse);
		node.setRenderState(ms);
	}


	private void addContainerNodesToNode(
			List<XmoContainerNode> containerNodes, Node rootNode, String entityName, XmoEntity entity) {
		for (XmoContainerNode xmoContainerNode : containerNodes) {
			Node containerNode = new Node("container");
			if (xmoContainerNode.getScale() != null) {
				containerNode.setScale(xmoContainerNode.getScale());
			}

			if (xmoContainerNode.getPosition() != null) {
				containerNode.setTranslation(xmoContainerNode.getPosition());
			}

			if (xmoContainerNode.getRotation() != null) {
				containerNode.setRotation(xmoContainerNode.getRotation());
			}

			rootNode.attachChild(containerNode);

			if (xmoContainerNode.getExtendedMeshObjects() != null) {
				addExtendedMeshObjectsToNode(xmoContainerNode.getExtendedMeshObjects(), containerNode, entityName, entity);
			}
			addContainerNodesToNode(xmoContainerNode.getContainerNodes(), containerNode, entityName, entity);
		}
	}


	private void addExtendedMeshObjectsToNode(
			List<ExtendedMeshObject> children, Node parentNode, String entityName, final XmoEntity entity) {
		for (ExtendedMeshObject xmo : children) {
			ColladaContents colladaContents = colladaContentLoader.get("/" + xmo.getColladaFile());

			Node node = colladaContents.getScene().makeCopy(true);

			if (xmo.getPhysicsFile() != null) {
				setPhysicsOnEntity(entity, xmo.getPhysicsFile(), xmo.getScale());
			}

			if (xmo.getScale() != null) {
				node.setScale(xmo.getScale());
			}

			if (xmo.getPosition() != null) {
				node.setTranslation(xmo.getPosition());
			}

			if (xmo.getRotation() != null) {
				node.setRotation(xmo.getRotation());
			}

			if (xmo.getTextureFile() != null) {
				xmoTextureManager.applyTexture(xmo.getTextureFile(), node);
			}

			if (xmo.getXmoMaterialFile() != null) {
				xmoMaterialManager.applyMaterial(xmo.getXmoMaterialFile(), node);
			}

			parentNode.attachChild(node);

		}
	}

	private SkinData cloneSkinData(SkinData skinData, Node model) {
		SkeletonPose clonedPose = new SkeletonPose(skinData.getPose().getSkeleton());
		SkinData clonedSkinData = new SkinData(skinData.getName());
		clonedSkinData.setSkinBaseNode(model);
		clonedSkinData.setPose(clonedPose);
		return clonedSkinData;
	}

	private void setPhysicsOnEntity(final XmoEntity entity, final String physicsFile, Vector3 scale) {
		//log.debug("Loading physics mesh from file: {}", physicsFile);
		ColladaContents colladaContents = colladaContentLoader.get("/" + physicsFile);
		Node physicsNode = colladaContents.getScene().makeCopy(true);
		physicsNode.setScale(scale);
		physicsNode.acceptVisitor(new Visitor() {
			@Override
			public void visit(Spatial spatial) {
				if (spatial instanceof Mesh) {
					Mesh mesh = (Mesh) spatial;
					mesh.setName("physicsmesh " + physicsFile);
					entity.addPhysicsMesh(mesh);
				}
			}
		}, true);
	}

}