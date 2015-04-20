package se.spaced.spacedit.ui.view.xmotree.menus.impl;

import com.google.inject.Singleton;
import se.fearlessgames.common.ui.swing.ActionDispatcher;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoMetaNodeMenuView;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

@Singleton
public class XmoMetaNodeMenuImpl extends JPopupMenu implements XmoMetaNodeMenuView {
	private final ActionDispatcher deleteActionDispatcher = new ActionDispatcher();

	public XmoMetaNodeMenuImpl() {


		JMenuItem deleteMenuItem = new JMenuItem("Delete");
		deleteMenuItem.addActionListener(deleteActionDispatcher);


		add(new Separator());
		add(deleteMenuItem);
	}

	@Override
	public void show(JTree tree, int x, int y) {
		super.show(tree, x, y);
	}


	@Override
	public void setDeleteAction(se.fearlessgames.common.ui.Action action) {
		deleteActionDispatcher.setAction(action);
	}
}