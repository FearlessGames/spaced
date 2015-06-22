package se.spaced.server.mob.brains.templates;

import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.tools.spawnpattern.view.InputType;

public interface BrainParameter {
	Class<? extends BrainTemplate> getBrain();

	String getName();

	Object retrieveValue(MobTemplate mobTemplate, MobSpawnTemplate mobSpawnTemplate);

	void updateValue(
			MobSpawnTemplate mobSpawnTemplate,
			Object parameter);

	InputType getType();

	boolean isEditable();
}
