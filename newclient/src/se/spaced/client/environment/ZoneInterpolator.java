package se.spaced.client.environment;

import se.ardortech.math.SpacedVector3;
import se.spaced.client.environment.settings.EmptyEnvSettings;
import se.spaced.client.environment.settings.EnvSettings;
import se.spaced.client.environment.settings.EnvSettingsImpl;
import se.spaced.client.environment.settings.FogSetting;
import se.spaced.client.environment.settings.SoundSetting;
import se.spaced.client.environment.settings.SunSetting;
import se.spaced.shared.resources.zone.Zone;

import java.util.HashMap;
import java.util.Map;

public class ZoneInterpolator {
	private final SpacedVector3 pos;

	static class Pair {
		final Zone zone;
		final EnvSettings envSettings;
		double weight = 1.0;

		Pair(Zone zone, EnvSettings envSettings) {
			this.zone = zone;
			this.envSettings = envSettings;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			Pair pair = (Pair) o;

			if (!zone.equals(pair.zone)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return zone.hashCode();
		}
	}

	private final Map<Zone, Pair> zones = new HashMap<Zone, Pair>();

	public void multiplyWeight(Zone zone, double weightFactor) {
		Pair pair = zones.get(zone);
		if (pair != null) {
			pair.weight *= weightFactor;
		}
	}


	public ZoneInterpolator(SpacedVector3 pos) {
		this.pos = pos;
	}

	public void addZone(Zone zone, EnvSettings zoneEnvSettings) {
		zones.put(zone, new Pair(zone, zoneEnvSettings));
	}

	public EnvSettings getSettings() {
		double totalWeight = 0;
		SunSetting sunSetting = EmptyEnvSettings.INSTANCE.getSunSetting(); 
		FogSetting fogSetting = EmptyEnvSettings.INSTANCE.getFogSetting(); 
		SoundSetting soundSetting = EmptyEnvSettings.INSTANCE.getSoundSetting();

		double maxWeight = 0;
		
		for (Pair pair : zones.values()) {
			double weight = pair.weight * pair.zone.getEnvironmentWeight(pos);
			if (weight > 0) {
				totalWeight += weight;
				
				float relativeWeight = (float) (weight / totalWeight);
				EnvSettings envSettings = pair.envSettings;

				sunSetting = sunSetting.interpolate(envSettings.getSunSetting(), relativeWeight);
				fogSetting = fogSetting.interpolate(envSettings.getFogSetting(), relativeWeight);
				
				if (weight > maxWeight) {
					maxWeight = weight;
					soundSetting = envSettings.getSoundSetting();
				}
			}
		}
		return new EnvSettingsImpl(sunSetting, fogSetting, soundSetting);
	}
}
