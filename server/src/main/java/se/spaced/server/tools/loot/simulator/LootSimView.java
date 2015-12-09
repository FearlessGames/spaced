package se.spaced.server.tools.loot.simulator;

import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.tools.loot.HasPanel;

import javax.swing.*;

public interface LootSimView extends HasPanel {

	void setPresenter(Presenter presenter);

	void setTemplates(PersistableLootTemplate[] templates);

	JTextArea getTextArea();

	interface Presenter {
		void onSearch(PersistableLootTemplate selectedItem);

		void onReload();
	}
}
