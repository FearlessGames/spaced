package se.spaced.client.resources.zone;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Sphere;
import se.mockachino.matchers.matcher.*;
import se.spaced.client.model.Prop;
import se.spaced.shared.resources.zone.Zone;

import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;
import static se.mockachino.matchers.MatchersBase.mAny;

public class ScenegraphZoneActivationListenerTest {

	private ScenegraphZoneActivationListener scenegraphZoneActivationListener;
	private ScenegraphService scenegraphService;

	@Before
	public void setUp() throws Exception {
		scenegraphService = mock(ScenegraphService.class);
		scenegraphZoneActivationListener = new ScenegraphZoneActivationListener(scenegraphService);
	}

	@Test
	public void emptyZoneWasLoaded() throws Exception {
		Zone zone = new Zone("Empty", new Sphere(new SpacedVector3(0, 0, 0), 100));
		scenegraphZoneActivationListener.zoneWasLoaded(zone);
		verifyNever().on(scenegraphService).addProp(any(Prop.class), zone);
	}

	@Test
	public void zoneWasLoaded() throws Exception {
		Zone zone = new Zone("Simple", new Sphere(new SpacedVector3(0, 0, 0), 100));
		Prop prop1 = new Prop("foo", new SpacedVector3(1, 2, 3), new SpacedVector3(1, 1, 1), SpacedRotation.IDENTITY);
		zone.addProp(prop1);
		Prop prop2 = new Prop("bar", new SpacedVector3(1, 2, 3), new SpacedVector3(1, 1, 1), SpacedRotation.IDENTITY);
		zone.addProp(prop2);
		scenegraphZoneActivationListener.zoneWasLoaded(zone);
		verifyOnce().on(scenegraphService).addProp(prop1, zone);
		verifyOnce().on(scenegraphService).addProp(prop2, zone);
		verifyOnce().on(scenegraphService).attachNode(zone.getNode());
	}

	@Test
	public void emptyZoneWasUnloaded() throws Exception {
		Zone zone = new Zone("Empty", new Sphere(new SpacedVector3(0, 0, 0), 100));
		scenegraphZoneActivationListener.zoneWasUnloaded(zone);
		ArgumentCatcher<Collection> catcher = ArgumentCatcher.create(mAny(Collection.class));
		verifyOnce().on(scenegraphService).removeProps(match(catcher));
		assertTrue(catcher.getValue().isEmpty());

		verifyOnce().on(scenegraphService).detachNode(zone.getNode());
	}
}
