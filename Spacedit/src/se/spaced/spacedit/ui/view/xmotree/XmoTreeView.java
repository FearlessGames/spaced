package se.spaced.spacedit.ui.view.xmotree;

import se.fearlessgames.common.ui.Action;
import se.spaced.spacedit.ui.view.xmotree.menus.XmoMenu;
import se.spaced.spacedit.xmo.model.WrappedXmoRoot;

public interface XmoTreeView {
	public void setNodeSelectedAction(Action as);

	public Object getSelectedElement();

	void buildTree(WrappedXmoRoot xmoRoot);

	void selectNode(Object node);

	public void setShowMenuAction(ShowMenuAction showMenuAction);

	public void showMenu(XmoMenu xmoMenu, int x, int y);
}