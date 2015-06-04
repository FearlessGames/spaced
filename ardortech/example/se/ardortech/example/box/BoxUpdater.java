package se.ardortech.example.box;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Updater;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.util.Constants;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.stat.StatCollector;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BoxUpdater implements Updater {
	private final BoxScene scene;
	private final Vector3 boxRotationAxis = new Vector3(1, 1, 0.5f).normalizeLocal();
	private final Matrix3 boxRotation = new Matrix3();

	@Inject
	public BoxUpdater(BoxScene scene) {
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

		boxRotation.fromAngleNormalAxis(timer.getTimeInSeconds(), boxRotationAxis);
		scene.getBox().setRotation(boxRotation);
	}
}