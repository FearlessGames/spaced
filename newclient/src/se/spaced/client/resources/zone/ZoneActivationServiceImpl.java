package se.spaced.client.resources.zone;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Sphere;
import se.spaced.shared.resources.zone.Zone;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ZoneActivationServiceImpl implements ZoneActivationService {
	private final Set<Zone> loadedZones = Sets.newHashSet();
	private final Lock reloadLock = new ReentrantLock();
	private Zone rootZone;
	private final ZoneActivationListener zoneActivationListener;
	private final ZoneChangedListener zoneChangedListener;
	private Zone lastActiveZone;

	@Inject
	public ZoneActivationServiceImpl(ZoneActivationListener zoneActivationListener, ZoneChangedListener zoneChangedListener) {
		this.zoneActivationListener = zoneActivationListener;
		this.zoneChangedListener = zoneChangedListener;
		rootZone = new Zone("The void", new Sphere(SpacedVector3.ZERO, 0));
	}

	@Override
	public void setRootZone(Zone rootZone, SpacedVector3 position, double range) {
		reloadLock.lock();
		try {
			this.rootZone = rootZone;
		} finally {
			reloadLock.unlock();
		}
	}

	@Override
	public Zone getMostActiveZone(SpacedVector3 position) {
		List<Zone> activeZones = getActiveZones(position);
		return activeZones.get(activeZones.size() - 1);
	}

	@Override
	public Zone getRootZone() {
		return rootZone;
	}

	@Override
	public List<Zone> getActiveZones(SpacedVector3 point) {
		List<Zone> zones = Lists.newArrayList();
		Zone zone = rootZone;
		while (zone != null) {
			zones.add(zone);
			zone = zone.getSubzoneAt(point, 0);
		}
		return zones;
	}

	@Override
	public Set<Zone> getNearbyZones(SpacedVector3 point, double margin) {
		return getNearbyZones(rootZone, point, margin);
	}

	@Override
	public Set<Zone> getNearbyZones(Zone root, SpacedVector3 point, double margin) {
		Set<Zone> zones = Sets.newHashSet();
		traverse(root, zones, point, -margin);
		return zones;
	}

	@Override
	public void update(SpacedVector3 point, double nearbyRange) {
		boolean locked = reloadLock.tryLock();
		if (!locked) {
			return;
		}
		try {
			internalUpdate(point, nearbyRange);
		} finally {
			reloadLock.unlock();
		}
	}

	private void internalUpdate(SpacedVector3 point, double nearbyRange) {
		Set<Zone> nearbyZones = getNearbyZones(point, nearbyRange);

		Iterator<Zone> zoneIterator = loadedZones.iterator();
		while (zoneIterator.hasNext()) {
				Zone zone = zoneIterator.next();
				if (!nearbyZones.contains(zone)) {
					zoneActivationListener.zoneWasUnloaded(zone);
					zoneIterator.remove();
				}
			}

		for (Zone zone : nearbyZones) {
				if (!loadedZones.contains(zone)) {
					zoneActivationListener.zoneWasLoaded(zone);
					loadedZones.add(zone);
				}
			}

		List<Zone> activeZones = this.getActiveZones(point);
		Zone mostActiveZone = activeZones.get(activeZones.size() - 1);
		if (!mostActiveZone.equals(lastActiveZone)) {
				zoneChangedListener.zoneChanged(lastActiveZone, mostActiveZone);
				lastActiveZone = mostActiveZone;
			}
	}

	private void traverse(Zone zone, Collection<Zone> zones, SpacedVector3 point, double margin) {
		if (zone.isInside(point, margin)) {
			zones.add(zone);
			for (Zone subzone : zone.getSubzones()) {
				traverse(subzone, zones, point, margin);
			}
		}
	}
}
