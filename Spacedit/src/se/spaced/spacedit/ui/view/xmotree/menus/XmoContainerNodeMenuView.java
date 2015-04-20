package se.spaced.spacedit.ui.view.xmotree.menus;

import se.fearlessgames.common.ui.Action;

public interface XmoContainerNodeMenuView extends XmoMenu {
	public void setAddXmoAction(Action action);

	public void setAddContainerNodeAction(Action action);

	public void setDeleteAction(Action action);
}