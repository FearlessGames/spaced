package se.spaced.spacedit.xmo.model;

import com.ardor3d.scenegraph.Node;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.model.xmo.MetaNode;
import se.spaced.shared.model.xmo.XmoMetaNode;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.spacedit.xmo.XmoManagerListener;
import se.spaced.spacedit.xmo.model.listeners.XmoMetaNodePropertyListener;

public class WrappedXmoMetaNode implements NodeHolder, MovableXmo {
	private final ListenerDispatcher<XmoMetaNodePropertyListener> propertyChangedDispatcher = ListenerDispatcher.create(XmoMetaNodePropertyListener.class);
	private final XmoMetaNodeOwner owner;
	private MetaNode metaNode;
	private Node node;

	public WrappedXmoMetaNode(MetaNode metaNode, XmoMetaNodeOwner owner) {
		this.metaNode = metaNode;
		this.owner = owner;
	}

	@Override
	public void updateLocation(double x, double y, double z) {
		final SpacedVector3 size = metaNode.getSize();
		final SpacedVector3 rotation = metaNode.getRotation();

		metaNode = new XmoMetaNode(new SpacedVector3(x, y, z), size, rotation);
		propertyChangedDispatcher.trigger().onLocationChange(x, y, z);
	}

	public SpacedVector3 getPosition() {
		return metaNode.getPosition();
	}

	@Override
	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public Node getNode() {
		return node;
	}

	@Override
	public XmoType getXmoType() {
		return XmoType.XmoMetaNode;
	}

	@Override
	public void triggerSelectedDispatch(XmoManagerListener xmoManagerListener) {
		xmoManagerListener.selectedXmoMetaNode(this);
	}

	@Override
	public WrappedExtendedMeshObject createExtendedMeshObject() {
		return null;
	}

	@Override
	public WrappedXmoContainerNode createXmoContainerNode() {
		return null;
	}

	@Override
	public NodeHolder delete() {
		return owner.delete(this);
	}
}
