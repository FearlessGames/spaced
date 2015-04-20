package se.spaced.mvp.view;

import se.spaced.mvp.Action;

public interface MainMenuView {
	void setOpenFileAction(Action action);

	void setSaveFileAction(Action action);

	void setQuitAction(Action action);
}
