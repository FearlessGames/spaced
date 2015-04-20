package se.spaced.spacedit.ui.view.xmotree.menus.impl;

import com.google.inject.Singleton;
import se.fearlessgames.common.ui.swing.ActionDispatcher;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoRootMenuView;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

@Singleton
public class XmoRootMenuImpl extends JPopupMenu implements XmoRootMenuView {
	private final ActionDispatcher addXmoActionDispatcher = new ActionDispatcher();
	private final ActionDispatcher addContainerNodeActionDispatcher = new ActionDispatcher();

	public XmoRootMenuImpl() {
		JMenuItem addXmoMenuItem = new JMenuItem("Add Collada");
		addXmoMenuItem.addActionListener(addXmoActionDispatcher);

		JMenuItem addContainerNodeMenuItem = new JMenuItem("Add ContainerNode");
		addContainerNodeMenuItem.addActionListener(addContainerNodeActionDispatcher);

		add(addXmoMenuItem);
		add(addContainerNodeMenuItem);
	}

	@Override
	public void show(JTree tree, int x, int y) {
		super.show(tree, x, y);
	}

	@Override
	public void setAddXmoAction(se.fearlessgames.common.ui.Action action) {
		addXmoActionDispatcher.setAction(action);
	}

	@Override
	public void setAddContainerNodeAction(se.fearlessgames.common.ui.Action action) {
		addContainerNodeActionDispatcher.setAction(action);
	}
}
