package se.spaced.client.resources.zone;

import se.spaced.client.model.Prop;
import se.spaced.shared.resources.zone.Zone;

public interface ZoneValidator {
	boolean validateZone(Zone zone);

	boolean validateProp(Prop prop);
}
