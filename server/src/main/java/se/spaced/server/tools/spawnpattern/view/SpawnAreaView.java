package se.spaced.server.tools.spawnpattern.view;

import se.fearless.common.uuid.UUID;

import javax.swing.*;

public interface SpawnAreaView extends IsPanel {
	void setPresenter(Presenter presenter);

	void setSpawnAreaPanel(JPanel panel);

	void setUUID(UUID pk);

	Class<?> askForAreaType(Class<?>[] possibilities, Class<?> preselected);

	public interface Presenter {

		void changeAreaType();
	}


}
