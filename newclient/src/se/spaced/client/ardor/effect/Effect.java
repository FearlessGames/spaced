package se.spaced.client.ardor.effect;

public interface Effect extends EffectNode {
	void addEffectComponent(EffectComponent<? extends Effect> effectComponent);

	void start();

	void stop();

	void update(double dt);
}
