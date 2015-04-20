package se.spaced.client.resources.zone;

import se.ardortech.math.SpacedVector3;
import se.spaced.shared.resources.zone.Zone;

import java.util.List;
import java.util.Set;

public interface ZoneActivationService {


	void update(SpacedVector3 point, double range);

	/**
	 * First item is the root zone, last item is the most specific zone
	 *
	 * @param point
	 * @return
	 */
	List<Zone> getActiveZones(SpacedVector3 point);

	Set<Zone> getNearbyZones(SpacedVector3 point, double range);

	Set<Zone> getNearbyZones(Zone root, SpacedVector3 point, double range);

	void setRootZone(Zone rootZone, SpacedVector3 position, double range);

	Zone getMostActiveZone(SpacedVector3 position);

	Zone getRootZone();
}
