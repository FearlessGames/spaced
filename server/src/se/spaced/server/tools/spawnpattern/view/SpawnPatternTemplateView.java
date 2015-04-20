package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.model.spawn.MobSpawnTemplate;

public interface SpawnPatternTemplateView extends IsPanel {

	void setPresenter(Presenter presenter);

	void setUUID(String uuid);

	void setPatternName(String name);

	void setMobSpawns(Iterable<MobSpawnTemplate> mobspawns);

	void selectMobSpawnTemplate(MobSpawnTemplate mobSpawnTemplate);

	public interface Presenter {
		void selectedMobSpawnTemplate(MobSpawnTemplate mobSpawnTemplate);

		void removeSpawnTemplate(MobSpawnTemplate mobSpawnTemplate);

		void changeNameOnCurrentPattern(String name);

		void addMobSpawnTemplate();
	}
}
