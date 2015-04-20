package se.spaced.client.ardor.effect;

public interface EffectBuilder<T extends Effect> {
	T buildEffect(EffectContext effectContect);
}
