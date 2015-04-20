package se.spaced.client.environment.settings;

import se.spaced.client.environment.time.GameTime;

import java.util.Map;
import java.util.NavigableMap;

public class KeyFrameSettingsInterpolator<T extends Interpolable<T>> {

	private final NavigableMap<GameTime, T> keyframes;
	private final GameTime cycleTime;
	private final String name;

	public KeyFrameSettingsInterpolator(String name, GameTime cycleTime, NavigableMap<GameTime, T> keyframes) {
		this.name = name;
		this.cycleTime = cycleTime;
		this.keyframes = keyframes;
		if (keyframes.isEmpty()) {
			throw new IllegalArgumentException("Settings must contain atleast one " + this.name + "setting");
		}
	}

	public T getSettings(GameTime t) {
		GameTime t1;
		Map.Entry<GameTime, T> lower = keyframes.floorEntry(t);
		if (lower == null) {
			lower = keyframes.lastEntry();
			t1 = lower.getKey().subtract(cycleTime);
		} else {
			t1 = lower.getKey();
		}
		GameTime t2;
		Map.Entry<GameTime, T> upper = keyframes.ceilingEntry(t);
		if (upper == null) {
			upper = keyframes.firstEntry();
			t2 = upper.getKey().add(cycleTime);
		} else {
			t2 = upper.getKey();
		}

		T from = lower.getValue();
		T to = upper.getValue();

		long diff = t2.getValue() - t1.getValue();
		if (diff == 0) {
			return from;
		}

		float pos = ((float) t.getValue() - (float) t1.getValue()) / (float) diff;
		return from.interpolate(to, pos);
	}

}