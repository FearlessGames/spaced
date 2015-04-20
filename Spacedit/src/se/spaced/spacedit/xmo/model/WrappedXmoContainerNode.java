package se.spaced.spacedit.xmo.model;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import se.spaced.shared.model.xmo.ExtendedMeshObject;
import se.spaced.shared.model.xmo.XmoContainerNode;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.spacedit.xmo.XmoManagerListener;
import se.spaced.spacedit.xmo.model.listeners.XmoContainerNodePropertyListener;

import java.util.ArrayList;
import java.util.List;

public class WrappedXmoContainerNode implements NodeHolder, MovableXmo, ExtendedMeshObjectOwner, XmoContainerNodeOwner {
	private final ListenerDispatcher<XmoContainerNodePropertyListener> propertyChangedDispatcher = ListenerDispatcher.create(XmoContainerNodePropertyListener.class);
	private final XmoContainerNodeOwner owner;
	private final XmoContainerNode xmoContainerNode;
	private final List<WrappedExtendedMeshObject> extendedMeshObjects = new ArrayList<WrappedExtendedMeshObject>();
	private final List<WrappedXmoContainerNode> containerNodes = new ArrayList<WrappedXmoContainerNode>();

	private Node node;

	public WrappedXmoContainerNode(XmoContainerNode xmoContainerNode, XmoContainerNodeOwner owner) {
		this.owner = owner;
		this.xmoContainerNode = xmoContainerNode;

		if (xmoContainerNode.getExtendedMeshObjects() == null) {
			xmoContainerNode.setExtendedMeshObjects(new ArrayList<ExtendedMeshObject>());
		}

		for (ExtendedMeshObject extendedMeshObject : xmoContainerNode.getExtendedMeshObjects()) {
			extendedMeshObjects.add(new WrappedExtendedMeshObject(extendedMeshObject, this));
		}


		if (xmoContainerNode.getContainerNodes() == null) {
			xmoContainerNode.setContainerNodes(new ArrayList<XmoContainerNode>());
		}

		for (XmoContainerNode containerNode : xmoContainerNode.getContainerNodes()) {
			containerNodes.add(new WrappedXmoContainerNode(containerNode, this));
		}
	}

	@Override
	public void updateLocation(double x, double y, double z) {
		xmoContainerNode.getPosition().set(x, y, z);
		propertyChangedDispatcher.trigger().onLocationChange(x, y, z);
	}

	public Vector3 getPosition() {
		return xmoContainerNode.getPosition();
	}

	public void setPosition(Vector3 position) {
		xmoContainerNode.setPosition(position);
	}

	public Quaternion getRotation() {
		return xmoContainerNode.getRotation();
	}

	public void setRotation(Quaternion rotation) {
		xmoContainerNode.setRotation(rotation);
	}

	public Vector3 getScale() {
		return xmoContainerNode.getScale();
	}

	public void setScale(Vector3 scale) {
		xmoContainerNode.setScale(scale);
	}

	public List<WrappedExtendedMeshObject> getExtendedMeshObjects() {
		return extendedMeshObjects;
	}


	public List<WrappedXmoContainerNode> getContainerNodes() {
		return containerNodes;
	}


	public Node getNode() {
		return node;
	}

	@Override
	public XmoType getXmoType() {
		return XmoType.XmoContainerNode;
	}

	@Override
	public void triggerSelectedDispatch(XmoManagerListener xmoManagerListener) {
		xmoManagerListener.selectedXmoContainerNode(this);
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void updateRotation(double x, double y, double z, double w) {
		xmoContainerNode.getRotation().set(x, y, z, w);
		propertyChangedDispatcher.trigger().onRotationChange(x, y, z, w);
	}

	public void updateScale(double x, double y, double z) {
		xmoContainerNode.getScale().set(x, y, z);
		propertyChangedDispatcher.trigger().onScaleChange(x, y, z);
	}

	@Override
	public String toString() {
		return "ContainerNode";
	}

	@Override
	public WrappedExtendedMeshObject createExtendedMeshObject() {
		WrappedExtendedMeshObject extendedMeshObject = new WrappedExtendedMeshObject(new ExtendedMeshObject(), this);
		xmoContainerNode.getExtendedMeshObjects().add(extendedMeshObject.getExtendedMeshObject());
		extendedMeshObjects.add(extendedMeshObject);
		return extendedMeshObject;
	}

	@Override
	public WrappedXmoContainerNode createXmoContainerNode() {
		WrappedXmoContainerNode containerNode = new WrappedXmoContainerNode(new XmoContainerNode(), this);
		containerNodes.add(containerNode);
		xmoContainerNode.getContainerNodes().add(containerNode.getXmoContainerNode());
		return containerNode;
	}

	public XmoContainerNode getXmoContainerNode() {
		return xmoContainerNode;
	}

	public void addPropertyChangeListener(XmoContainerNodePropertyListener xmoContainerNodePropertyListener) {
		propertyChangedDispatcher.addListener(xmoContainerNodePropertyListener);
	}

	@Override
	public NodeHolder delete(WrappedExtendedMeshObject wrappedExtendedMeshObject) {
		xmoContainerNode.getExtendedMeshObjects().remove(wrappedExtendedMeshObject.getExtendedMeshObject());
		extendedMeshObjects.remove(wrappedExtendedMeshObject);
		return this;
	}

	@Override
	public NodeHolder delete(WrappedXmoContainerNode containerNode) {
		xmoContainerNode.getContainerNodes().remove(containerNode.getXmoContainerNode());
		containerNodes.remove(containerNode);
		return this;
	}

	@Override
	public NodeHolder delete() {
		return owner.delete(this);
	}


}
