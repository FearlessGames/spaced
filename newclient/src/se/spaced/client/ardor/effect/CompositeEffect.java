package se.spaced.client.ardor.effect;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Preconditions.checkArgument;

public class CompositeEffect implements Effect {
	private final Collection<Effect> effects = new CopyOnWriteArrayList<Effect>();
	private EffectNode parent;

	public CompositeEffect(Iterable<Effect> effects) {
		Iterator<Effect> effectIterator = effects.iterator();
		checkArgument(effectIterator.hasNext(), "Needs at least one effect");
		for (Effect effect : effects) {
			effect.setParent(this);
			this.effects.add(effect);
		}
	}

	@Override
	public void addEffectComponent(EffectComponent<? extends Effect> effectComponent) {
		throw new UnsupportedOperationException("A composite effect can't have components (yet?)");
	}

	@Override
	public void start() {
		for (Effect effect : effects) {
			effect.start();
		}
	}

	@Override
	public void stop() {
		for (Effect effect : effects) {
			effect.stop();
		}
	}

	@Override
	public void update(double dt) {
		for (Effect effect : effects) {
			effect.update(dt);
		}
	}

	@Override
	public void setParent(EffectNode effectNode) {
		parent = effectNode;
	}

	@Override
	public void detachChild(EffectNode effectNode) {
		effects.remove(effectNode);
		if (effects.isEmpty()) {
			parent.detachChild(this);
		}
	}
}
