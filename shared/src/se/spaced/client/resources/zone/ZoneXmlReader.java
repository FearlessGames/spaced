package se.spaced.client.resources.zone;

import se.spaced.shared.resources.zone.Zone;

public interface ZoneXmlReader {
	Zone loadRootZone(String zoneFile);
}
