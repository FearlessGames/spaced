package se.spaced.client.ardor;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.Updater;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import se.ardortech.BaseArdorMain;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.time.TimeProvider;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.GameStateHandler;

public class Spaced extends BaseArdorMain {
	private static final int WANTED_FPS = 59;
	private static final long SLEEP_TIME = 1000 / WANTED_FPS;
	private final TimeProvider timeProvider;
	private final GameStateHandler gameStateHandler;
	private final LifetimeManager lifetimeManager;
	private final GameState startState;

	@Inject
	public Spaced(
			FrameHandler frameHandler, NativeCanvas canvas,
			Updater updater,
			TimeProvider timeProvider,
			GameStateHandler gameStateHandler,
			LifetimeManager lifetimeManager,
			@Named("startState") GameState startState) {
		super(frameHandler, canvas, updater);
		this.timeProvider = timeProvider;
		this.gameStateHandler = gameStateHandler;
		this.lifetimeManager = lifetimeManager;
		this.startState = startState;
	}

	@Override
	public void run() {
		lifetimeManager.start();
		gameStateHandler.changeState(startState);
		super.run();
	}

	@Override
	protected void update() {
		long beforeRender = timeProvider.now();

		super.update();
		long waitTime = SLEEP_TIME - (timeProvider.now() - beforeRender);
		if (waitTime > 0) {
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		lifetimeManager.shutdown();
	}
}