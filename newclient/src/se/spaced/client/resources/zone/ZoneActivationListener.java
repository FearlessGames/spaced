package se.spaced.client.resources.zone;

import se.spaced.shared.resources.zone.Zone;

public interface ZoneActivationListener {
	void zoneWasLoaded(Zone zone);
	void zoneWasUnloaded(Zone zone);
}
