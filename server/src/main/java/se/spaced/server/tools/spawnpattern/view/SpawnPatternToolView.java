package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.model.spawn.SpawnPatternTemplate;

public interface SpawnPatternToolView {
	void setPresenter(Presenter presenter);

	void addSpawnPatternTemplate(SpawnPatternTemplate spawnPatternTemplate);

	void setVisible(boolean bool);

	void selectSpawnPatternTemplate(SpawnPatternTemplate spawnPatternTemplate);

	void showExportedXml(String title, String xml);

	interface Presenter {
		void show();

		void selectedSpawnPattern(SpawnPatternTemplate spawnPatternTemplate);

		void saveTemplates();

		void createNewTemplate(String name);

		void toXml();

		void removeSelectedSpawnPattern(SpawnPatternTemplate spawnPatternTemplate);
	}
}
