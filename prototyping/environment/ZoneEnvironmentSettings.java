import java.util.List;

public class ZoneEnvironmentSettings {

	List<SunSetting> sunSetting;

	public SunSetting getSunSetting(long time) {
		SunSetting previous = getPreviousSunSetting();
		SunSetting next = getNextSunSetting(time);
		return next.getInterpolatedSetting(time, next.occursAt, previous);
	}

	private SunSetting getNextSunSetting(long time) {
		return null;
	}

	private SunSetting getPreviousSunSetting() {
		return null;
	}
}
