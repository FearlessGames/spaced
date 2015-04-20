package se.spaced.client.ardor.effect;

public interface EffectNode {
	void setParent(EffectNode effectNode);

	void detachChild(EffectNode effectNode);
}
