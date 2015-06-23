package se.spaced.client.ardor.effect;

import se.spaced.client.core.states.Updatable;

import java.util.concurrent.Future;

public interface EffectSystem extends Updatable {
	void startEffect(Effect effect);

	Future<Effect> startEffect(String effectName, EffectContext context);

	Future<Effect> loadEffect(String effectName, EffectContext context, Callback<Effect> callback);
}
