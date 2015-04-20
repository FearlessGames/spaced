package se.spaced.spacedit.xmo;

import se.spaced.spacedit.xmo.model.WrappedExtendedMeshObject;
import se.spaced.spacedit.xmo.model.WrappedXmoContainerNode;
import se.spaced.spacedit.xmo.model.WrappedXmoMetaNode;

public interface XmoManagerListener {
	void loadedXmoRoot();

	void selectedXmoRoot();

	void selectedXmoMetaNode(WrappedXmoMetaNode xmoMetaNode);

	void selectedXmoContainerNode(WrappedXmoContainerNode xmoContainerNode);

	void selectedExtendedMeshObject(WrappedExtendedMeshObject extendedMeshObject);

	void addedNewXmoObject();

	void deletedXmoObject();
}
