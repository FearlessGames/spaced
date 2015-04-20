package se.spaced.shared.scheduler;

import se.krka.kahlua.integration.annotations.LuaConstructor;
import se.krka.kahlua.integration.annotations.LuaMethod;

public class Timer {
	private long delay;
	private long startTime;
	private long endTime;
	private boolean autoRestart;

	public Timer() {

	}

	@LuaConstructor(name = "NewTimer")
	public Timer(long delay, boolean autoReset) {
		this.delay = delay;
		this.autoRestart = autoReset;
	}


	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public boolean isAutoRestart() {
		return autoRestart;
	}

	public void setAutoRestart(boolean autoRestart) {
		this.autoRestart = autoRestart;
	}

	@LuaMethod()
	public void start() {
		startTime = System.currentTimeMillis();
		endTime = startTime + delay;
	}

	@LuaMethod()
	public boolean hasElapsed() {
		if (System.currentTimeMillis() > endTime) {
			if (autoRestart) {
				start();
			}
			return true;
		}
		return false;

	}

}
