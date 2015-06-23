package se.spaced.client.resources.zone;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Sphere;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;


public class ZoneActivationServiceImplTest {
	private ZoneActivationService zoneActivationService;
	private ZoneActivationListener zoneActivationListener;
	private Zone rootZone;
	private Zone planetC;
	private Zone planetB;
	private Zone planetA;

	@Before
	public void setup() {
		rootZone = new Zone("Universe", new Sphere(SpacedVector3.ZERO, 1000));
		planetA = new Zone("Planet A", new Sphere(new SpacedVector3(-300, -200, 0), 300));
		rootZone.addSubZone(planetA);
		planetB = new Zone("Planet B", new Sphere(new SpacedVector3(-300, 200, 0), 300));
		rootZone.addSubZone(planetB);
		planetC = new Zone("Planet C", new Sphere(new SpacedVector3(200, 0, 0), 200));
		rootZone.addSubZone(planetC);
		zoneActivationListener = mock(ZoneActivationListener.class);
		zoneActivationService = new ZoneActivationServiceImpl(zoneActivationListener, ListenerDispatcher.create(ZoneChangedListener.class).trigger());
		zoneActivationService.setRootZone(rootZone, SpacedVector3.ZERO, 0);
	}

	@Test
	public void testSimpleLoading() {
		List<Zone> activeZones = zoneActivationService.getActiveZones(new SpacedVector3(10000, 10000, 0));
		assertEquals(1, activeZones.size());
		assertEquals(rootZone, activeZones.get(0));
	}

	@Test
	public void testSlightlyLessSimpleLoading() {
		SpacedVector3 playerPos = new SpacedVector3(50, 50, 0);
		List<Zone> activeZones = zoneActivationService.getActiveZones(playerPos);
		assertEquals(2, activeZones.size());
		assertEquals(rootZone, activeZones.get(0));
		assertEquals(planetC, activeZones.get(1));
	}

	@Test
	public void testRange() {
		SpacedVector3 playerPos = new SpacedVector3(50, 50, 0);
		Collection<Zone> hits = zoneActivationService.getNearbyZones(playerPos, 100.0);
		assertTrue(hits.contains(rootZone));
		assertTrue(hits.contains(planetB));
		assertTrue(hits.contains(planetC));
		assertEquals(3, hits.size());
	}

	@Test
	public void testUpdate() {
		zoneActivationService.update(new SpacedVector3(-300, 0, 0), 300.0);
		verifyExactly(3).on(zoneActivationListener).zoneWasLoaded(any(Zone.class));
		verifyExactly(1).on(zoneActivationListener).zoneWasLoaded(same(rootZone));
		verifyExactly(1).on(zoneActivationListener).zoneWasLoaded(same(planetA));
		verifyExactly(1).on(zoneActivationListener).zoneWasLoaded(same(planetB));
		verifyExactly(0).on(zoneActivationListener).zoneWasLoaded(same(planetC));

		getData(zoneActivationListener).resetCalls();
		zoneActivationService.update(new SpacedVector3(50, 50, 0), 1000.0);
		verifyExactly(1).on(zoneActivationListener).zoneWasLoaded(any(Zone.class));
		verifyExactly(1).on(zoneActivationListener).zoneWasLoaded(same(planetC));

		getData(zoneActivationListener).resetCalls();
		zoneActivationService.update(new SpacedVector3(5000, 5000, 0), 10.0);
		verifyExactly(4).on(zoneActivationListener).zoneWasUnloaded(any(Zone.class));
		verifyExactly(1).on(zoneActivationListener).zoneWasUnloaded(same(rootZone));
		verifyExactly(1).on(zoneActivationListener).zoneWasUnloaded(same(planetA));
		verifyExactly(1).on(zoneActivationListener).zoneWasUnloaded(same(planetB));
		verifyExactly(1).on(zoneActivationListener).zoneWasUnloaded(same(planetC));

		getData(zoneActivationListener).resetCalls();
		zoneActivationService.update(new SpacedVector3(200, 10, 0), 15.0);
		verifyExactly(2).on(zoneActivationListener).zoneWasLoaded(any(Zone.class));
		verifyExactly(1).on(zoneActivationListener).zoneWasLoaded(same(rootZone));
		verifyExactly(1).on(zoneActivationListener).zoneWasLoaded(same(planetC));

	}
}
