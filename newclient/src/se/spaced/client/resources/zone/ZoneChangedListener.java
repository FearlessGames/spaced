package se.spaced.client.resources.zone;

import se.spaced.shared.resources.zone.Zone;

public interface ZoneChangedListener {
	void zoneChanged(Zone old, Zone newZone);
}
