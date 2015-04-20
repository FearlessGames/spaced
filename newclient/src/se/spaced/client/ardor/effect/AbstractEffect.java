package se.spaced.client.ardor.effect;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class AbstractEffect implements Effect {
	private final List<EffectComponent<? extends Effect>> effectComponents = Lists.newArrayList();
	protected EffectNode parent;

	@Override
	public void addEffectComponent(EffectComponent<? extends Effect> effectComponent) {
		effectComponents.add(effectComponent);
	}

	@Override
	public void start() {
		for (EffectComponent component : effectComponents) {
			component.onStart(this);
		}
	}

	@Override
	public void stop() {
		for (EffectComponent component : effectComponents) {
			component.onStop(this);
		}
	}

	@Override
	public void update(double dt) {
		for (EffectComponent component : effectComponents) {
			component.onUpdate(dt, this);
		}
	}

	@Override
	public void setParent(EffectNode effectNode) {
		parent = effectNode;
	}

	@Override
	public void detachChild(EffectNode effectNode) {
		parent.detachChild(effectNode);
	}
}
