package se.spaced.server.tools.loot.xml;

import se.spaced.server.tools.loot.HasPanel;

public interface LootXmlView extends HasPanel {
	void setXml(String xml);

	void setPresenter(Presenter presenter);

	interface Presenter {
		void onCreateAndShowXml();
	}
}
