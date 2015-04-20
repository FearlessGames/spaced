package se.spaced.spacedit.ui.view.xmotree.menus.impl;

import com.google.inject.Singleton;
import se.fearlessgames.common.ui.Action;
import se.fearlessgames.common.ui.swing.ActionDispatcher;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoXmoMenuView;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

@Singleton
public class XmoXmoMenuImpl extends JPopupMenu implements XmoXmoMenuView {
	private final ActionDispatcher deleteActionDispatcher = new ActionDispatcher();
	private final ActionDispatcher addMetaNodeActionDispatcher = new ActionDispatcher();

	public XmoXmoMenuImpl() {
		JMenuItem metaNodeMaterialMenu = new JMenuItem("Add MetaNode");
		metaNodeMaterialMenu.addActionListener(addMetaNodeActionDispatcher);

		JMenuItem deleteMenuItem = new JMenuItem("Delete");
		deleteMenuItem.addActionListener(deleteActionDispatcher);

		add(metaNodeMaterialMenu);
		add(new Separator());
		add(deleteMenuItem);
	}

	@Override
	public void show(JTree tree, int x, int y) {
		super.show(tree, x, y);
	}


	@Override
	public void setAddMetaNodeAction(Action action) {
		addMetaNodeActionDispatcher.setAction(action);
	}

	@Override
	public void setDeleteAction(Action action) {
		deleteActionDispatcher.setAction(action);
	}
}