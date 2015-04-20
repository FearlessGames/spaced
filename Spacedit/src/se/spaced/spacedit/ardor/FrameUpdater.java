package se.spaced.spacedit.ardor;

import se.ardortech.Main;

public interface FrameUpdater extends Main {
	public void run();

	public boolean isPaused();

	public void setPaused(boolean paused);
}
