package se.spaced.client.ardor.effect;

public class AsynchEffect {
	private final EffectContext effectContext;
	private final String effectName;
	private final EffectSystem effectSystem;

	private volatile Effect effect;
	private volatile State state = State.INIT;
	private volatile boolean running;

	private enum State {
		INIT, WAITING, NOT_STARTED, STARTED
	}

	public AsynchEffect(EffectContext effectContext, String effectName, EffectSystem effectSystem) {
		this.effectContext = effectContext;
		this.effectName = effectName;
		this.effectSystem = effectSystem;
	}

	public void start() {
		switch (state) {
			case INIT:
				requestEffect();
				break;
			case WAITING:
				break;
			case NOT_STARTED:
				effectSystem.startEffect(effect);
				state = State.STARTED;
				break;
			case STARTED:
				break;
		}
		running = true;
	}

	private void requestEffect() {
		state = State.WAITING;
		effectSystem.loadEffect(effectName, effectContext, new Callback<Effect>() {
			@Override
			public void onReady(Effect result) {
				effect = result;
				state = State.NOT_STARTED;
				if (running) {
					start();
				}
			}
		});
	}

	public void stop() {
		if (state == State.STARTED && running) {
			effect.stop();
		}
		running = false;
	}
}
