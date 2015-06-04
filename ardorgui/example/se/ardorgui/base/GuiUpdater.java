package se.ardorgui.base;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Updater;
import com.ardor3d.util.Constants;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.stat.StatCollector;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GuiUpdater implements Updater {
	private final GuiScene scene;

	@Inject
	public GuiUpdater(GuiScene scene) {
		this.scene = scene;
	}

	@Override
	@MainThread
	public void init() {
		scene.init();
	}

	@Override
	@MainThread
	public void update(final ReadOnlyTimer timer) {
		if (Constants.stats) {
			StatCollector.update();
		}
	}
}