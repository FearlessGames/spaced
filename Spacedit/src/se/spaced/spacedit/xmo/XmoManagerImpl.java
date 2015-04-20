package se.spaced.spacedit.xmo;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.extension.model.collada.jdom.data.AssetData;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Sphere;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.shared.model.xmo.XmoRoot;
import se.spaced.shared.resources.XmoMaterialManager;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;
import se.spaced.spacedit.ardor.DefaultScene;
import se.spaced.spacedit.state.RunningState;
import se.spaced.spacedit.state.StateManager;
import se.spaced.spacedit.xmo.model.NodeHolder;
import se.spaced.spacedit.xmo.model.WrappedExtendedMeshObject;
import se.spaced.spacedit.xmo.model.WrappedXmoContainerNode;
import se.spaced.spacedit.xmo.model.WrappedXmoMetaNode;
import se.spaced.spacedit.xmo.model.WrappedXmoRoot;
import se.spaced.spacedit.xmo.model.XmoType;
import se.spaced.spacedit.xmo.model.listeners.ExtendedMeshObjectPropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoContainerNodePropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoMetaNodePropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoRootPropertyListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class XmoManagerImpl implements XmoManager {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private ListenerDispatcher<XmoManagerListener> xmoManagerDispatcher = ListenerDispatcher.create(XmoManagerListener.class);

	private final XmlIO xmlIO;
	private final StreamLocator streamLocator;
	private final ColladaImporter colladaImporter;

	private final XmoRootPropertyListener xmoRootPropertyListener;
	private final ExtendedMeshObjectPropertyListener xmoPropertyListener;
	private final XmoContainerNodePropertyListener xmoContainerNodePropertyListener;
	private final XmoMetaNodePropertyListener xmoMetaNodePropertyListener;

	private final StateManager stateManager;


	private final DefaultScene defaultScene;

	private final String colladaDir;
	private final XmoMaterialManager xmoMaterialManager;

	private WrappedXmoRoot xmoRoot;
	private NodeHolder currentlySelectedObject;

	private Map<Node, NodeHolder> nodeHolderMap = new HashMap<Node, NodeHolder>();
	private Node xmoRootNode;
	private static final String STATIC_COLLADA_PATH = "static/collada/";

	@Inject
	public XmoManagerImpl(final XmlIO xmlIO, final StreamLocator streamLocator, final StateManager stateManager, final XmoRootPropertyListener xmoRootPropertyListener,
								 final ExtendedMeshObjectPropertyListener xmoPropertyListener,
								 final XmoContainerNodePropertyListener xmoContainerNodePropertyListener, final XmoMetaNodePropertyListener xmoMetaNodePropertyListener,
								 final DefaultScene defaultScene, ColladaImporter colladaImporter, @Named("colladaDir") final String colladaDir,
								 final XmoMaterialManager xmoMaterialManager) {
		this.xmlIO = xmlIO;
		this.streamLocator = streamLocator;
		this.xmoRootPropertyListener = xmoRootPropertyListener;
		this.stateManager = stateManager;
		this.xmoPropertyListener = xmoPropertyListener;
		this.xmoContainerNodePropertyListener = xmoContainerNodePropertyListener;
		this.xmoMetaNodePropertyListener = xmoMetaNodePropertyListener;
		this.defaultScene = defaultScene;
		this.colladaImporter = colladaImporter;
		this.colladaDir = colladaDir;
		this.xmoMaterialManager = xmoMaterialManager;


	}

	public WrappedXmoRoot getRoot() {
		return xmoRoot;
	}

	@Override
	public void saveXmoRoot() {
		try {
			xmlIO.save(xmoRoot.getXmoRoot(), xmoRoot.getFilePath());
		} catch (XmlIOException e) {
			log.error("Failed to save xmoRoot", e);
		}
	}

	@Override
	public void addListener(XmoManagerListener listener) {
		xmoManagerDispatcher.addListener(listener);
	}

	private void connectNodeWithHolder(NodeHolder nodeHolder, Node node) {
		nodeHolder.setNode(node);
		nodeHolderMap.put(node, nodeHolder);
	}

	private void disconnectNodeWithNodeHolder(Node node) {
		nodeHolderMap.remove(node);
	}

	public NodeHolder getCurrentlySelectedObject() {
		return currentlySelectedObject;
	}

	@Override
	public void deleteSelectedObject() {
		NodeHolder owner = currentlySelectedObject.delete();
		disconnectNodeWithNodeHolder(currentlySelectedObject.getNode());
		owner.getNode().detachChild(currentlySelectedObject.getNode());
		xmoManagerDispatcher.trigger().deletedXmoObject();
		selectObject(owner);
	}

	@Override
	public NodeHolder getObject(Node node) {
		return nodeHolderMap.get(node);
	}

	@Override
	public XmoType getCurrentSelectedType() {
		return XmoType.getType(currentlySelectedObject);
	}

	@Override
	public Node getCurrentNode() {
		return currentlySelectedObject.getNode();
	}


	@Override
	public void initXmoRoot(WrappedXmoRoot xmoRoot) {
		this.xmoRoot = xmoRoot;

		xmoRoot.addPropertyChangeListener(xmoRootPropertyListener);

		Node sceneRootNode = defaultScene.getRootNode();

		xmoRootNode = new Node(xmoRoot.getName());
		connectNodeWithHolder(xmoRoot, xmoRootNode);

		sceneRootNode.attachChild(xmoRootNode);

		addExtendedMeshObjectsToNode(xmoRoot.getExtendedMeshObjects(), xmoRootNode);
		addContainerNodesToNode(xmoRoot.getContainerNodes(), xmoRootNode);

		xmoManagerDispatcher.trigger().loadedXmoRoot();
		selectXmoRoot();

		stateManager.switchState(RunningState.XMO_IN_CONTEXT);
	}

	public void selectObject(NodeHolder object) {
		currentlySelectedObject = object;
		object.triggerSelectedDispatch(xmoManagerDispatcher.trigger());
	}

	@Override
	public void selectXmoRoot() {
		currentlySelectedObject = xmoRoot;
		xmoManagerDispatcher.trigger().selectedXmoRoot();
	}


	@Override
	public void loadXmoRoot(String xmoRootPath) {
		try {
			xmoRoot = new WrappedXmoRoot(xmlIO.load(XmoRoot.class, xmoRootPath), xmoRootPath);
			initXmoRoot(xmoRoot);
		} catch (XmlIOException e) {
			log.error("Failed to load xmo root", e);
		}
	}

	final AtomicInteger n = new AtomicInteger(0);

	@Override
	public void addColladaFileAsXmo(String colladaFile) {
		ColladaStorage colladaStorage = null;
		try {
			colladaStorage = colladaImporter.load(colladaFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Node node = colladaStorage.getScene();
		AssetData assetData = colladaStorage.getAssetData();
		log.debug("Got up axis: {}", assetData.getUpAxis());
		ReadOnlyVector3 upAxis = assetData.getUpAxis();
		if (upAxis.getZ() != 0) {
			Quaternion rot = new Quaternion(0, 0, 0, 0.5);
			node.setRotation(rot);
		}

		WrappedExtendedMeshObject extendedMeshObject = currentlySelectedObject.createExtendedMeshObject();

		extendedMeshObject.setColladaFile(STATIC_COLLADA_PATH + colladaFile);
		extendedMeshObject.addPropertyChangeListener(xmoPropertyListener);

		connectNodeWithHolder(extendedMeshObject, node);

		Node currentNode = getCurrentNode();
		currentNode.attachChild(node);

		xmoManagerDispatcher.trigger().addedNewXmoObject();
		selectObject(extendedMeshObject);
	}

	@Override
	public Node getXmoRootNode() {
		return xmoRootNode;
	}

	@Override
	public void createNewContainerNode() {
		WrappedXmoContainerNode containerNode = currentlySelectedObject.createXmoContainerNode();
		if (containerNode == null) {
			return;
		}
		containerNode.addPropertyChangeListener(xmoContainerNodePropertyListener);

		Node node = new Node();
		connectNodeWithHolder(containerNode, node);
		currentlySelectedObject.getNode().attachChild(node);
		xmoManagerDispatcher.trigger().addedNewXmoObject();
		selectObject(containerNode);
	}

	@Override
	public void createNewMetaNode() {
	}

	@Override
	public void reloadMaterial(String materialFile) {
		log.debug("trying to load material {}", materialFile);
		Node node = currentlySelectedObject.getNode();
		log.debug("got node: {}", node.getName());
		xmoMaterialManager.applyMaterial(materialFile, node);

	}

	private void addContainerNodesToNode(List<WrappedXmoContainerNode> containerNodes, Node parentNode) {
		for (WrappedXmoContainerNode containerNode : containerNodes) {
			Node node = new Node();
			if (containerNode.getScale() != null) {
				node.setScale(containerNode.getScale());
			}

			if (containerNode.getPosition() != null) {
				node.setTranslation(containerNode.getPosition());
			}

			if (containerNode.getRotation() != null) {
				node.setRotation(containerNode.getRotation());
			}
			connectNodeWithHolder(containerNode, node);
			parentNode.attachChild(node);
			addExtendedMeshObjectsToNode(containerNode.getExtendedMeshObjects(), node);
			addContainerNodesToNode(containerNode.getContainerNodes(), node);
		}
	}


	private void addExtendedMeshObjectsToNode(List<WrappedExtendedMeshObject> children, Node parentNode) {
		for (WrappedExtendedMeshObject extendedMeshObject : children) {
			extendedMeshObject.addPropertyChangeListener(xmoPropertyListener);
			//TODO: FIX.
			String type = extendedMeshObject.getColladaFile().substring(extendedMeshObject.getColladaFile().lastIndexOf('.'));
			Node node = null;
			try {
				node = colladaImporter.load(extendedMeshObject.getColladaFile()).getScene();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (node != null) {

				if (extendedMeshObject.getScale() != null) {
					node.setScale(extendedMeshObject.getScale());
				}

				if (extendedMeshObject.getPosition() != null) {
					node.setTranslation(extendedMeshObject.getPosition());
				}

				if (extendedMeshObject.getRotation() != null) {
					node.setRotation(extendedMeshObject.getRotation());
				}

				if (extendedMeshObject.getXmoMaterialFile() != null) {
					xmoMaterialManager.applyMaterial(extendedMeshObject.getXmoMaterialFile(), node);
				}

				connectNodeWithHolder(extendedMeshObject, node);
				parentNode.attachChild(node);

			} else {
				log.warn("Could not find jdom file when loading xmo: {}", extendedMeshObject.getColladaFile());
			}

		}
	}

	private void addMetaNodesToNode(List<WrappedXmoMetaNode> metaNodes, Node parentNode) {
		for (WrappedXmoMetaNode metaNode : metaNodes) {
			Node node = new Node();

			Sphere s = new Sphere("metanode", 4, 4, 4);
			s.setSolidColor(ColorRGBA.YELLOW);
			node.attachChild(s);

			if (metaNode.getPosition() != null) {
				node.setTranslation(metaNode.getPosition());
			}
			connectNodeWithHolder(metaNode, node);
			parentNode.attachChild(node);
		}
	}

}
