package se.spaced.client.resources.zone;

import se.spaced.shared.resources.zone.Zone;

public interface ZoneXmlWriter {
	void saveZone(String zoneFile, Zone zone);
}