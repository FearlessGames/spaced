package se.spaced.spacedit.ardor;

import com.ardor3d.framework.FrameHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.util.TimeProvider;

@Singleton
public class FixedFpsFrameUpdater implements FrameUpdater {
	private static final int WANTED_FPS = 45;
	private static final long SLEEP_TIME = 1000 / WANTED_FPS;

	private final FrameHandler frameHandler;
	private final TimeProvider timeProvider;
	private volatile boolean running;
	private volatile boolean paused;

	@Inject
	public FixedFpsFrameUpdater(FrameHandler frameHandler, TimeProvider timeProvider) {
		this.frameHandler = frameHandler;
		this.timeProvider = timeProvider;
		running = true;
	}

	@Override
	public void run() {
		while (running) {
			long beforeRender = timeProvider.now();
			if (!paused) {
				frameHandler.updateFrame();
			}
			long waitTime = SLEEP_TIME - (timeProvider.now() - beforeRender);
			if (waitTime > 0) {
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
				}
			} else {
				Thread.yield();
			}
		}
	}

	@Override
	public void exit() {
		running = false;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
}