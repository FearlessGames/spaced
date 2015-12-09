package se.spaced.server.tools.spawnpattern.view;

import com.google.common.collect.ImmutableSet;
import se.spaced.server.mob.brains.templates.BrainParameter;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;

public interface BrainParameterView extends IsPanel {

	void setPresenter(Presenter presenter);

	void setBrainParameters(
			ImmutableSet<BrainParameter> brainParameters,
			MobTemplate mobTemplate,
			MobSpawnTemplate mobSpawnTemplate);

	interface Presenter {
		void changeGeometryData();
	}
}
