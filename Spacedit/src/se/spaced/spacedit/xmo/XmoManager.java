package se.spaced.spacedit.xmo;

import com.ardor3d.scenegraph.Node;
import se.spaced.spacedit.xmo.model.NodeHolder;
import se.spaced.spacedit.xmo.model.WrappedXmoRoot;
import se.spaced.spacedit.xmo.model.XmoType;

public interface XmoManager {
	void addListener(XmoManagerListener listener);

	void selectXmoRoot();

	WrappedXmoRoot getRoot();

	void selectObject(NodeHolder object);

	NodeHolder getObject(Node node);

	public XmoType getCurrentSelectedType();

	public NodeHolder getCurrentlySelectedObject();

	public void deleteSelectedObject();

	Node getCurrentNode();

	void initXmoRoot(WrappedXmoRoot xmoRoot);

	void saveXmoRoot();

	void loadXmoRoot(String xmoRootResource);

	void addColladaFileAsXmo(String xmoRootResource);

	Node getXmoRootNode();

	void createNewContainerNode();

	void createNewMetaNode();

	void reloadMaterial(String materialFile);
}
