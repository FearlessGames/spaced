package se.spaced.spacedit.ui.view.frame;

import se.fearlessgames.common.ui.Action;

public interface MainView {
	//Actions
	void setQuitButtonAction(Action quitButtonAction);

	//Boilerplate
	void start() throws Exception;

	public void shutDown();
}

