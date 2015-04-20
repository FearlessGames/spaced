package se.spaced.spacedit.ui.view.xmotree;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.infonode.docking.View;
import se.spaced.spacedit.ui.tdi.TdiChildWindow;
import se.spaced.spacedit.ui.view.utils.swing.SwingThread;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoMenu;
import se.spaced.spacedit.xmo.model.WrappedExtendedMeshObject;
import se.spaced.spacedit.xmo.model.WrappedXmoContainerNode;
import se.spaced.spacedit.xmo.model.WrappedXmoMetaNode;
import se.spaced.spacedit.xmo.model.WrappedXmoRoot;
import se.spaced.spacedit.xmo.model.XmoType;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.List;

@Singleton
public class XmoTreeViewImpl extends JPanel implements XmoTreeView, TdiChildWindow, TreeSelectionListener {
	private JTree tree;
	private final View view;
	private se.fearlessgames.common.ui.Action nodeSelectedAction;
	private ShowMenuAction showMenuAction;

	@Inject
	public XmoTreeViewImpl() {
		super(new GridLayout(1, 0));
		view = new View("XMO Tree", null, this);
		view.getWindowProperties().setCloseEnabled(false);

		tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("No Xmo Loaded")));
		tree.setEditable(false);
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		add(new JScrollPane(tree));

		MouseListener mouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					handlePopupMenuShow(e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					handlePopupMenuShow(e.getX(), e.getY());
				}
			}
		};
		tree.addMouseListener(mouseListener);
	}

	private void handlePopupMenuShow(int x, int y) {
		int selRow = tree.getRowForLocation(x, y);
		if (selRow == -1) {
			return;
		}
		TreePath selPath = tree.getPathForLocation(x, y);
		tree.setSelectionPath(selPath);
		Object object = getSelectedElement();
		if (showMenuAction != null) {
			showMenuAction.show(XmoType.getType(object), x, y);
		}
	}

	@SwingThread
	@Override
	public void buildTree(WrappedXmoRoot xmoRoot) {
		tree.removeAll();

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(xmoRoot);

		addXmoMeshes(rootNode, xmoRoot.getExtendedMeshObjects());
		addContainerNodes(rootNode, xmoRoot.getContainerNodes());

		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		tree.setModel(treeModel);
	}

	private void addContainerNodes(DefaultMutableTreeNode treeNode, List<WrappedXmoContainerNode> containerNodes) {
		for (WrappedXmoContainerNode containerNode : containerNodes) {
			DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(containerNode);
			treeNode.add(subNode);
			addXmoMeshes(subNode, containerNode.getExtendedMeshObjects());
			addContainerNodes(subNode, containerNode.getContainerNodes());

		}
	}

	private void addXmoMeshes(DefaultMutableTreeNode treeNode, List<WrappedExtendedMeshObject> extendedMeshObjects) {
		for (WrappedExtendedMeshObject extendedMeshObject : extendedMeshObjects) {
			DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(extendedMeshObject);
			treeNode.add(subNode);
		}
	}

	private void addMetaNodes(DefaultMutableTreeNode treeNode, List<WrappedXmoMetaNode> metaNodes) {
		for (WrappedXmoMetaNode metaNode : metaNodes) {
			DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(metaNode);
			treeNode.add(subNode);
		}
	}

	@Override
	public View getTdiView() {
		return view;
	}

	@Override
	public void setNodeSelectedAction(se.fearlessgames.common.ui.Action as) {
		nodeSelectedAction = as;
	}

	@SwingThread
	@Override
	public void selectNode(Object userObject) {
		if (tree.getLastSelectedPathComponent() != null) {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (treeNode.getUserObject() == userObject) {
				return; //ignore if the node is already selected
			}
		}

		DefaultMutableTreeNode node = findNodeForUserObject(userObject);
		if (node != null) {
			tree.setSelectionPath(new TreePath(node.getPath()));
		}

	}

	private DefaultMutableTreeNode findNodeForUserObject(Object obj) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		Enumeration enumeration = root.depthFirstEnumeration();
		while (enumeration.hasMoreElements()) {
			Object o = enumeration.nextElement();
			if (o instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) o;
				if (dmtn.getUserObject() == obj) {
					return dmtn;
				}
			}
		}
		return null;
	}

	@Override
	public Object getSelectedElement() {
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		return treeNode.getUserObject();
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}

		if (nodeSelectedAction != null) {
			nodeSelectedAction.act();
		}
	}

	public void setShowMenuAction(ShowMenuAction showMenuAction) {
		this.showMenuAction = showMenuAction;
	}

	@Override
	public void showMenu(XmoMenu xmoMenu, int x, int y) {
		xmoMenu.show(tree, x, y);
	}
}