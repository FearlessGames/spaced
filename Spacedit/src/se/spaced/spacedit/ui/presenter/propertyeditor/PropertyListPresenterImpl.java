package se.spaced.spacedit.ui.presenter.propertyeditor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.spacedit.ui.view.propertylist.PropertyListView;
import se.spaced.spacedit.ui.view.propertylist.XmoContainerPropertyList;
import se.spaced.spacedit.ui.view.propertylist.XmoElementPropertyList;
import se.spaced.spacedit.ui.view.propertylist.XmoMetaElementPropertyList;
import se.spaced.spacedit.ui.view.propertylist.XmoRootElementPropertyList;
import se.spaced.spacedit.xmo.XmoManager;
import se.spaced.spacedit.xmo.XmoManagerListener;
import se.spaced.spacedit.xmo.model.WrappedExtendedMeshObject;
import se.spaced.spacedit.xmo.model.WrappedXmoContainerNode;
import se.spaced.spacedit.xmo.model.WrappedXmoMetaNode;

@Singleton
public class PropertyListPresenterImpl implements PropertyListPresenter, XmoManagerListener {
	private final PropertyListView propertyListView;
	private final XmoManager xmoManager;

	@Inject
	public PropertyListPresenterImpl(final PropertyListView propertyListView, final XmoManager xmoManager) {
		this.propertyListView = propertyListView;
		this.xmoManager = xmoManager;
		xmoManager.addListener(this);
	}

	@Override
	public void selectedExtendedMeshObject(WrappedExtendedMeshObject extendedMeshObject) {
		propertyListView.changePropertyList(new XmoElementPropertyList(extendedMeshObject, xmoManager));
	}


	@Override
	public void selectedXmoRoot() {
		propertyListView.changePropertyList(new XmoRootElementPropertyList(xmoManager.getRoot()));
	}

	@Override
	public void selectedXmoMetaNode(WrappedXmoMetaNode xmoMetaNode) {
		propertyListView.changePropertyList(new XmoMetaElementPropertyList(xmoMetaNode));
	}

	@Override
	public void selectedXmoContainerNode(WrappedXmoContainerNode xmoContainerNode) {
		propertyListView.changePropertyList(new XmoContainerPropertyList(xmoContainerNode));
	}


	@Override
	public void loadedXmoRoot() {
	}

	@Override
	public void addedNewXmoObject() {
	}

	@Override
	public void deletedXmoObject() {

	}


}
