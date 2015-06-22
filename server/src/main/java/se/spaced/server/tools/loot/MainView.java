package se.spaced.server.tools.loot;

import java.awt.Component;

public interface MainView {
	void addTabPanel(String title, Component component);

	void createAndShowUI();
}
