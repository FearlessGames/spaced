package se.spaced.client.ardor.effect;

public class EffectComponentAdapter<T extends Effect> implements EffectComponent<T> {
	@Override
	public void onStart(T effect) {
	}

	@Override
	public void onStop(T effect) {
	}

	@Override
	public void onUpdate(double dt, T effect) {
	}
}
