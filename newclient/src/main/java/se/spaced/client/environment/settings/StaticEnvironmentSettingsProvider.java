package se.spaced.client.environment.settings;

import se.spaced.client.environment.time.GameTime;

public class StaticEnvironmentSettingsProvider implements InterpolableProvider<EnvSettings> {
	private final EnvSettings settings;

	public StaticEnvironmentSettingsProvider(EnvSettings settings) {
		this.settings = settings;
	}

	@Override
	public EnvSettings getSettings(GameTime t) {
		return settings;
	}
}
