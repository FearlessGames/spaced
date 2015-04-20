package se.spaced.spacedit.xmo.model;

import com.ardor3d.scenegraph.Node;
import se.spaced.spacedit.xmo.XmoManagerListener;

public interface NodeHolder {
	public void setNode(Node node);

	public Node getNode();

	XmoType getXmoType();

	void triggerSelectedDispatch(XmoManagerListener xmoManagerListener);

	WrappedExtendedMeshObject createExtendedMeshObject();

	WrappedXmoContainerNode createXmoContainerNode();

	NodeHolder delete();
}
