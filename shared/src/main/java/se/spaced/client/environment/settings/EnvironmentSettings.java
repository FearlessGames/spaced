package se.spaced.client.environment.settings;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import se.spaced.client.environment.time.GameTime;

import java.util.HashMap;
import java.util.Map;

@XStreamAlias("environmentsettings")
public class EnvironmentSettings {
	private Map<GameTime, EnvSettings> settingsByGameTime;

	public EnvironmentSettings() {
		settingsByGameTime = new HashMap<GameTime, EnvSettings>();
	}

	public Map<GameTime, EnvSettings> getSettingsByGameTime() {
		return settingsByGameTime;
	}
}
