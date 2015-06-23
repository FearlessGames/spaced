package se.spaced.client.ardor.effect;

public interface EffectComponent<T extends Effect> {
	void onStart(T effect);

	void onStop(T effect);

	void onUpdate(double dt, T effect);
}
