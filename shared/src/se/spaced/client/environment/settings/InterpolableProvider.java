package se.spaced.client.environment.settings;

import se.spaced.client.environment.time.GameTime;

public interface InterpolableProvider<T extends Interpolable<T>> {
	T getSettings(GameTime t);
}
