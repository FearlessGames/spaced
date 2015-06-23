package se.spaced.client.environment;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.environment.settings.EnvSettings;
import se.spaced.client.environment.settings.Interpolable;
import se.spaced.client.environment.settings.InterpolableProvider;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.shared.resources.zone.Zone;

import java.util.Set;

@Singleton
public class ZoneInterpolationStrategy<T extends Interpolable<T>> {
	private final ZoneActivationService zoneActivationService;
	private final ZoneEnvironmentProvider zoneEnvironmentProvider;
	private static final int VISUAL_RANGE = 1000;

	@Inject
	public ZoneInterpolationStrategy(ZoneActivationService zoneActivationService, ZoneEnvironmentProvider zoneEnvironmentProvider) {
		this.zoneActivationService = zoneActivationService;
		this.zoneEnvironmentProvider = zoneEnvironmentProvider;
	}

	public EnvSettings getSettings(GameTime time, SpacedVector3 position) {
		Set<Zone> activeZones = zoneActivationService.getNearbyZones(position, 0);
		Set<Zone> allZones = zoneActivationService.getNearbyZones(position, VISUAL_RANGE);

		Zone root = zoneActivationService.getRootZone();

		ZoneInterpolator interpolator = new ZoneInterpolator(position);
		for (Zone zone : allZones) {
			InterpolableProvider<EnvSettings> provider = zoneEnvironmentProvider.getEnvironmentSettings(zone);
			if (provider != null) {
				interpolator.addZone(zone, provider.getSettings(time));
			}
		}
		for (Zone activeZone : activeZones) {
			double fade = activeZone.getInnerFade(position);
			if (fade < 1.0) {
				traverse(interpolator, root, activeZone, fade, position, -VISUAL_RANGE);
			}
		}

		return interpolator.getSettings();
	}

	private void traverse(ZoneInterpolator interpolator, Zone zone, Zone stopAt, double weightFactor, SpacedVector3 point, double margin) {
		if (zone.equals(stopAt)) {
			return;
		}
		if (zone.isInside(point, margin)) {
			interpolator.multiplyWeight(zone, weightFactor);
			for (Zone subzone : zone.getSubzones()) {
				traverse(interpolator, subzone, stopAt, weightFactor, point, margin);
			}
		}
	}
}
