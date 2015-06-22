package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.model.spawn.MobTemplate;

public interface AddMobTemplateDialogProvider {
	void show(AddMobTemplateDialogCallback addMobTemplateDialogCallback);

	public interface AddMobTemplateDialogCallback {
		void createNewSpawnTemplateForMob(MobTemplate mobTemplate);
	}
}
