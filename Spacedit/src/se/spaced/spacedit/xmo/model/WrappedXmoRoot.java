package se.spaced.spacedit.xmo.model;

import com.ardor3d.scenegraph.Node;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.model.xmo.ExtendedMeshObject;
import se.spaced.shared.model.xmo.XmoContainerNode;
import se.spaced.shared.model.xmo.XmoRoot;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.spacedit.xmo.XmoManagerListener;
import se.spaced.spacedit.xmo.model.listeners.XmoRootPropertyListener;

import java.util.ArrayList;
import java.util.List;

public class WrappedXmoRoot implements NodeHolder, ExtendedMeshObjectOwner, XmoContainerNodeOwner {
	private final ListenerDispatcher<XmoRootPropertyListener> propertyChangedDispatcher;
	private final XmoRoot xmoRoot;
	private final List<WrappedExtendedMeshObject> extendedMeshObjects;
	private final List<WrappedXmoContainerNode> containerNodes;

	private final String filePath;
	private Node node;

	public WrappedXmoRoot(XmoRoot xmoRoot, String filePath) {
		this.xmoRoot = xmoRoot;
		this.filePath = filePath;

		propertyChangedDispatcher = ListenerDispatcher.create(XmoRootPropertyListener.class);

		extendedMeshObjects = new ArrayList<WrappedExtendedMeshObject>();
		if (xmoRoot.getExtendedMeshObjects() == null) {
			xmoRoot.setExtendedMeshObjects(new ArrayList<ExtendedMeshObject>());
		}
		for (ExtendedMeshObject extendedMeshObject : xmoRoot.getExtendedMeshObjects()) {
			extendedMeshObjects.add(new WrappedExtendedMeshObject(extendedMeshObject, this));
		}


		containerNodes = new ArrayList<WrappedXmoContainerNode>();
		if (xmoRoot.getContainerNodes() == null) {
			xmoRoot.setContainerNodes(new ArrayList<XmoContainerNode>());
		}
		for (XmoContainerNode containerNode : xmoRoot.getContainerNodes()) {
			containerNodes.add(new WrappedXmoContainerNode(containerNode, this));
		}
	}

	public void addPropertyChangeListener(XmoRootPropertyListener propertyListener) {
		this.propertyChangedDispatcher.addListener(propertyListener);
	}

	public String getName() {
		return xmoRoot.getName();
	}

	public void setName(String name) {
		xmoRoot.setName(name);
		propertyChangedDispatcher.trigger().onNameChange(name);
	}

	public void updateSize(double x, double y, double z) {
		xmoRoot.setSize(new SpacedVector3(x, y, z));
		propertyChangedDispatcher.trigger().onSizeChange(x, y, z);
	}

	public SpacedVector3 getSize() {
		if (xmoRoot.getSize() == null) {
			xmoRoot.setSize(SpacedVector3.ZERO);
		}
		return xmoRoot.getSize();
	}


	public List<WrappedExtendedMeshObject> getExtendedMeshObjects() {
		return extendedMeshObjects;
	}

	public List<WrappedXmoContainerNode> getContainerNodes() {
		return containerNodes;
	}


	public XmoRoot getXmoRoot() {
		return xmoRoot;
	}

	public Node getNode() {
		return node;
	}

	@Override
	public XmoType getXmoType() {
		return XmoType.XmoRoot;

	}

	@Override
	public void triggerSelectedDispatch(XmoManagerListener xmoManagerListener) {
		xmoManagerListener.selectedXmoRoot();
	}

	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public String toString() {
		return xmoRoot.getName();
	}

	@Override
	public WrappedExtendedMeshObject createExtendedMeshObject() {
		WrappedExtendedMeshObject extendedMeshObject = new WrappedExtendedMeshObject(new ExtendedMeshObject(), this);
		xmoRoot.getExtendedMeshObjects().add(extendedMeshObject.getExtendedMeshObject());
		extendedMeshObjects.add(extendedMeshObject);
		return extendedMeshObject;
	}

	@Override
	public WrappedXmoContainerNode createXmoContainerNode() {
		WrappedXmoContainerNode containerNode = new WrappedXmoContainerNode(new XmoContainerNode(), this);
		xmoRoot.getContainerNodes().add(containerNode.getXmoContainerNode());
		containerNodes.add(containerNode);
		return containerNode;
	}

	@Override
	public NodeHolder delete() {
		return null;
	}

	@Override
	public NodeHolder delete(WrappedExtendedMeshObject wrappedExtendedMeshObject) {
		xmoRoot.getExtendedMeshObjects().remove(wrappedExtendedMeshObject.getExtendedMeshObject());
		extendedMeshObjects.remove(wrappedExtendedMeshObject);
		return this;
	}

	@Override
	public NodeHolder delete(WrappedXmoContainerNode xmoContainerNode) {
		xmoRoot.getContainerNodes().remove(xmoContainerNode.getXmoContainerNode());
		containerNodes.remove(xmoContainerNode);
		return this;
	}

	public String getFilePath() {
		return filePath;
	}
}
