public class EnvironmentSystem {
	ZoneEnvironmentSettings currentZoneEnvironmentSettings;
	Sun sun;

	public void update(long time) {
		sun.setCurrentSetting(currentZoneEnvironmentSettings.getSunSetting(time));
	}

}
