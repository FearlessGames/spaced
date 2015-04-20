package se.spaced.spacedit.ui.presenter.xmotree;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.ui.Action;
import se.spaced.spacedit.ui.presenter.filechooser.ColladaFileChooserPresenter;
import se.spaced.spacedit.ui.view.xmotree.ShowMenuAction;
import se.spaced.spacedit.ui.view.xmotree.XmoTreeView;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoContainerNodeMenuView;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoMetaNodeMenuView;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoRootMenuView;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoXmoMenuView;
import se.spaced.spacedit.xmo.XmoManager;
import se.spaced.spacedit.xmo.XmoManagerListener;
import se.spaced.spacedit.xmo.model.NodeHolder;
import se.spaced.spacedit.xmo.model.WrappedExtendedMeshObject;
import se.spaced.spacedit.xmo.model.WrappedXmoContainerNode;
import se.spaced.spacedit.xmo.model.WrappedXmoMetaNode;
import se.spaced.spacedit.xmo.model.XmoType;

@Singleton
public class XmoTreePresenterImpl implements XmoTreePresenter, XmoManagerListener {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final XmoTreeView xmoTreeView;

	private final XmoRootMenuView xmoRootMenuView;
	private final XmoXmoMenuView xmoXmoMenuView;
	private final XmoContainerNodeMenuView xmoContainerNodeMenuView;
	private final XmoMetaNodeMenuView xmoMetaNodeMenuView;

	private final XmoManager xmoManager;
	private final ColladaFileChooserPresenter colladaFileChooser;


	@Inject
	public XmoTreePresenterImpl(final XmoTreeView xmoTreeView, final XmoRootMenuView xmoRootMenuView, final XmoMetaNodeMenuView xmoMetaNodeMenuView,
										 final XmoXmoMenuView xmoXmoMenuView, final XmoContainerNodeMenuView xmoContainerNodeMenuView,
										 final XmoManager xmoManager, final ColladaFileChooserPresenter colladaFileChooser) {
		this.xmoTreeView = xmoTreeView;
		this.xmoRootMenuView = xmoRootMenuView;
		this.xmoXmoMenuView = xmoXmoMenuView;
		this.xmoContainerNodeMenuView = xmoContainerNodeMenuView;
		this.xmoMetaNodeMenuView = xmoMetaNodeMenuView;
		this.xmoManager = xmoManager;
		this.colladaFileChooser = colladaFileChooser;

		xmoManager.addListener(this);

		setupRootMenuActions();

		setupXmoContainerNodeMenuActions();

		setupXmoXmoMenuActions();

		setupXmoMetaNodeMenuActions();

		setupTreeActions();

	}


	private void setupTreeActions() {
		xmoTreeView.setNodeSelectedAction(new Action() {
			@Override
			public void act() {
				Object userObj = xmoTreeView.getSelectedElement();
				if (userObj instanceof NodeHolder) {
					xmoManager.selectObject((NodeHolder) userObj);
				}
			}
		});

		xmoTreeView.setShowMenuAction(new ShowMenuAction() {
			@Override
			public void show(XmoType type, int x, int y) {
				switch (type) {
					case ExtendedMeshObject:
						xmoTreeView.showMenu(xmoXmoMenuView, x, y);
						break;
					case XmoContainerNode:
						xmoTreeView.showMenu(xmoContainerNodeMenuView, x, y);
						break;
					case XmoRoot:
						xmoTreeView.showMenu(xmoRootMenuView, x, y);
						break;
					case XmoMetaNode: {
						xmoTreeView.showMenu(xmoMetaNodeMenuView, x, y);
					}
				}
			}
		});
	}

	private void setupRootMenuActions() {
		xmoRootMenuView.setAddContainerNodeAction(new Action() {
			@Override
			public void act() {
				xmoManager.createNewContainerNode();
			}
		});

		xmoRootMenuView.setAddXmoAction(new Action() {
			@Override
			public void act() {
				colladaFileChooser.loadColladaFile();
			}
		});
	}

	private void setupXmoContainerNodeMenuActions() {
		xmoContainerNodeMenuView.setAddContainerNodeAction(new Action() {
			@Override
			public void act() {
				xmoManager.createNewContainerNode();
			}
		});

		xmoContainerNodeMenuView.setAddXmoAction(new Action() {
			@Override
			public void act() {
				colladaFileChooser.loadColladaFile();
			}
		});

		xmoContainerNodeMenuView.setDeleteAction(new Action() {
			@Override
			public void act() {
				xmoManager.deleteSelectedObject();
			}
		});
	}

	private void setupXmoXmoMenuActions() {
		xmoXmoMenuView.setAddMetaNodeAction(new Action() {
			@Override
			public void act() {
				xmoManager.createNewMetaNode();
			}
		});

		xmoXmoMenuView.setDeleteAction(new Action() {
			@Override
			public void act() {
				xmoManager.deleteSelectedObject();
			}
		});
	}

	private void setupXmoMetaNodeMenuActions() {
		xmoMetaNodeMenuView.setDeleteAction(new Action() {
			@Override
			public void act() {
				xmoManager.deleteSelectedObject();
			}
		});
	}

	@Override
	public void loadedXmoRoot() {
		xmoTreeView.buildTree(xmoManager.getRoot());
	}

	@Override
	public void selectedXmoRoot() {
		xmoTreeView.selectNode(xmoManager.getRoot());
	}

	@Override
	public void selectedXmoMetaNode(WrappedXmoMetaNode xmoMetaNode) {
		xmoTreeView.selectNode(xmoMetaNode);
	}

	@Override
	public void selectedXmoContainerNode(WrappedXmoContainerNode xmoContainerNode) {
		xmoTreeView.selectNode(xmoContainerNode);
	}


	@Override
	public void selectedExtendedMeshObject(WrappedExtendedMeshObject extendedMeshObject) {
		xmoTreeView.selectNode(extendedMeshObject);
	}

	@Override
	public void addedNewXmoObject() {
		xmoTreeView.buildTree(xmoManager.getRoot());
	}

	@Override
	public void deletedXmoObject() {
		xmoTreeView.buildTree(xmoManager.getRoot());
	}

}
