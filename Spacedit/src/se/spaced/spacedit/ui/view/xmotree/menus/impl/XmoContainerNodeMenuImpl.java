package se.spaced.spacedit.ui.view.xmotree.menus.impl;

import com.google.inject.Singleton;
import se.fearlessgames.common.ui.swing.ActionDispatcher;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoContainerNodeMenuView;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

@Singleton
public class XmoContainerNodeMenuImpl extends JPopupMenu implements XmoContainerNodeMenuView {
	private final ActionDispatcher addXmoActionDispatcher = new ActionDispatcher();
	private final ActionDispatcher addContainerNodeActionDispatcher = new ActionDispatcher();
	private final ActionDispatcher deleteActionDispatcher = new ActionDispatcher();

	public XmoContainerNodeMenuImpl() {
		JMenuItem addXmoMenuItem = new JMenuItem("Add Collada");
		addXmoMenuItem.addActionListener(addXmoActionDispatcher);

		JMenuItem addContainerNodeMenuItem = new JMenuItem("Add ContainerNode");
		addContainerNodeMenuItem.addActionListener(addContainerNodeActionDispatcher);

		JMenuItem deleteMenuItem = new JMenuItem("Delete");
		deleteMenuItem.addActionListener(deleteActionDispatcher);

		add(addXmoMenuItem);
		add(addContainerNodeMenuItem);
		add(new Separator());
		add(deleteMenuItem);
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

	@Override
	public void setDeleteAction(se.fearlessgames.common.ui.Action action) {
		deleteActionDispatcher.setAction(action);
	}

}