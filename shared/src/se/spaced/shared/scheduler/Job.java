package se.spaced.shared.scheduler;

public class Job {
	private boolean loop;
	private Timer timer;
	private Invoker invokeTarget;

	public Job(long delay, boolean loop, Invoker invokeTarget) {
		this.timer = new Timer();
		timer.setDelay(delay);
		timer.setAutoRestart(loop);
		this.loop = loop;
		this.invokeTarget = invokeTarget;
	}

	public Job(long delay, boolean loop) {
		this(delay, loop, null);
	}

	public void setJob(Invoker invokeTarget) {
		this.invokeTarget = invokeTarget;
	}

	protected void start() {
		timer.start();
	}

	protected boolean hasElapsed() {
		return timer.hasElapsed();
	}

	protected void invoke() {
		invokeTarget.invoke();
	}

	protected boolean isLoop() {
		return loop;
	}
}
