package se.spaced.client.ardor.effect;

public interface EffectPrototype {
	EffectBuilder<? extends Effect> createBuilder();
}
