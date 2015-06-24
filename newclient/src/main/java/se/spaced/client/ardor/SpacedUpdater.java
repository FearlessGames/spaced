package se.spaced.client.ardor;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.extension.ui.UIHud;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.Updater;
import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.Constants;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.stat.StatCollector;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.spaced.client.core.MainTickerService;
import se.spaced.client.core.states.GameStateUpdater;

@Singleton
public class SpacedUpdater implements Updater {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Node rootNode;
	private final UIHud hud;
	private final GameStateUpdater stateUpdater;
	private final NativeCanvas canvas;
	private final SpacedScene scene;
	private final MainTickerService mainTickerService;
	private final Camera camera;

	private final FixedStep fixedStep = new FixedStep();
	private final TimeProvider timeProvider;


	// private final FixedTimestepTimer fixedTimeStepTimer = new FixedTimestepTimer();

	@Inject
	public SpacedUpdater(
			NativeCanvas canvas,
			SpacedScene scene,
			MainTickerService mainTickerService,
			GameStateUpdater gameStateUpdater,
			Camera camera,
			@Named("rootNode") Node rootNode,
			UIHud hud, TimeProvider timeProvider) {
		this.stateUpdater = gameStateUpdater;
		this.canvas = canvas;
		this.scene = scene;
		this.mainTickerService = mainTickerService;
		this.camera = camera;
		this.rootNode = rootNode;
		this.hud = hud;
		this.timeProvider = timeProvider;
	}

	@Override
	@MainThread
	public void init() {
		logger.debug("Init");
		canvas.setTitle("Spaced");
		scene.init();
		fixedStep.reset(timeProvider.now());
	}

	@Override
	@MainThread
	public void update(final ReadOnlyTimer timer) {
		if (Constants.stats) {
			StatCollector.update();
		}

		final double dt = timer.getTimePerFrame();

		mainTickerService.tick();	// Update GameLogic/Jobs
		stateUpdater.update(dt);
		fixedStep.update(timeProvider.now());
		scene.update(camera, dt);
		rootNode.updateGeometricState(dt, true);
		hud.updateGeometricState(dt);
	}


	class FixedStep {
		private static final long FRAMES_PER_SECOND = 60;
		private static final long MILLIS_PER_FRAME = 1000 / FRAMES_PER_SECOND;

		private long lastUpdate;

		public void update(long now) {
			while (now - lastUpdate >= MILLIS_PER_FRAME) {
				runFixedStep();
				lastUpdate += MILLIS_PER_FRAME;
			}
		}

		private void runFixedStep() {
			stateUpdater.updateFixed(MILLIS_PER_FRAME);
		}

		public void reset(long now) {
			lastUpdate = now;
		}
	}

}