package se.spaced.spacedit.ardor;

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.Updater;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.util.ReadOnlyTimer;
import com.google.inject.Inject;


public class DefaultUpdater implements Updater {
	private final DefaultScene scene;
	private final LogicalLayer logicalLayer;
	private final Canvas canvas;

	@Inject
	public DefaultUpdater(final DefaultScene scene, final LogicalLayer logicalLayer, Canvas canvas) {
		this.scene = scene;
		this.logicalLayer = logicalLayer;
		this.canvas = canvas;
	}

	@Override
	public void init() {
		scene.init();
	}

	@Override
	public void update(ReadOnlyTimer timer) {
		logicalLayer.checkTriggers(timer.getTimePerFrame());
		if (canvas.getCanvasRenderer().getCamera() != null) {
			scene.update(timer, canvas.getCanvasRenderer().getCamera());
		}
	}
}