package se.spaced.client.ardor.effect;

public abstract class AbstractEnvelopeComponent<T extends Effect> implements EffectComponent<T> {
	private final EffectComponent<? extends Effect> delegateComponent;
	private State state = State.STARTING;
	private double delayTime;
	private double playtime;
	private double releaseTime;

	protected AbstractEnvelopeComponent(
			EffectComponent<? extends Effect> delegateComponent, double delayTime, double playtime, double releaseTime) {
		this.delegateComponent = delegateComponent;
		this.delayTime = delayTime;
		this.playtime = playtime;
		this.releaseTime = releaseTime;
	}

	@Override
	public void onStop(final T effect) {
		state = State.RELEASING;
		onRelease(effect);
	}

	@Override
	public void onUpdate(final double dt, final T effect) {
		state.onUpdate(this, dt, effect);
	}

	public abstract void onRelease(T effect);

	public abstract void onReleasing(T effect, double dt);

	public abstract void onReleased(T effect);

	@SuppressWarnings("unchecked") // Everything sent to the state enum is already checked in AbstractEnvelopeComponent
	private enum State {
		STARTING {
			@Override
			void onUpdate(AbstractEnvelopeComponent parent, double dt, Effect effect) {
				parent.delayTime -= dt;
				if (parent.delayTime <= 0) {
					parent.delegateComponent.onStart(effect);
					parent.state = State.PLAYING;
				}
			}
		},
		PLAYING {
			@Override
			void onUpdate(AbstractEnvelopeComponent parent, double dt, Effect effect) {
				parent.playtime -= dt;
				if (parent.playtime <= 0) {
					parent.state = RELEASING;
					parent.onRelease(effect);
					return;
				}
				parent.delegateComponent.onUpdate(dt, effect);
			}
		},
		RELEASING {
			@Override
			void onUpdate(AbstractEnvelopeComponent parent, double dt, Effect effect) {
				parent.releaseTime -= dt;
				if (parent.releaseTime <= 0) {
					effect.detachChild(effect);
					parent.delegateComponent.onStop(effect);
					parent.onReleased(effect);
					return;
				}
				parent.onReleasing(effect, dt);
			}
		};

		abstract void onUpdate(AbstractEnvelopeComponent parent, double dt, Effect effect);
	}
}
