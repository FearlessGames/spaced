package se.spaced.server.tools.spawnpattern.presenter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.model.spawn.area.RandomSpaceSpawnArea;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.persistence.util.ServerXStreamRegistry;
import se.spaced.shared.world.area.Cube;
import se.spaced.shared.world.area.SinglePoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SpawnAreaFactoryTest {

	private SpawnAreaFactory spawnAreaFactory;
	private XStream xStream;

	@Before
	public void setup() {
		xStream = new XStream(new DomDriver());
		ServerXStreamRegistry registry = new ServerXStreamRegistry();
		registry.registerDefaultsOn(xStream);

		spawnAreaFactory = new SpawnAreaFactory(new GeometryFactory(xStream));

	}

	@Test
	public void testCreateSinglePointArea() throws SpawnAreaFactoryException {
		SinglePoint singlePoint = new SinglePoint(new SpacedVector3(1, 2, 3), new SpacedRotation(4, 5, 6, 7));
		String xml = xStream.toXML(singlePoint);
		SpawnArea area = spawnAreaFactory.createArea(SinglePointSpawnArea.class, xml);
		assertNotNull(area);
		assertNotNull(area.getPk());
		assertTrue(area instanceof SinglePointSpawnArea);
		SinglePoint spawnPoint = ((SinglePointSpawnArea) area).getSpawnPoint();
		assertEquals(singlePoint.getPoint(), spawnPoint.getPoint());
		assertEquals(singlePoint.getRotation(), spawnPoint.getRotation());
	}

	@Test
	public void testCreateRandomArea() throws SpawnAreaFactoryException {
		Cube cube = new Cube(new SpacedVector3(1, 2, 3), 10, 10, 10, new SpacedRotation(4, 5, 6, 7));
		String xml = xStream.toXML(cube);
		SpawnArea area = spawnAreaFactory.createArea(RandomSpaceSpawnArea.class, xml);
		assertNotNull(area);
		assertNotNull(area.getPk());
		assertTrue(area instanceof RandomSpaceSpawnArea);
		Cube newCube = ((RandomSpaceSpawnArea) area).getCube();
		assertEquals(cube.getCorner(), newCube.getCorner());
	}

	@Test
	public void testCreateAreaWithFaultyXml() {
		String xml = "sup son?";
		try {
			SpawnArea area = spawnAreaFactory.createArea(SinglePointSpawnArea.class, xml);
			fail();
		} catch (SpawnAreaFactoryException e) {
			assertEquals("se.spaced.server.tools.spawnpattern.presenter.GeometryException: Faulty Geometry XML",
					e.getMessage());
		}
	}

	@Test
	public void testCreateAreaWithUnknownType() {
		Cube cube = new Cube(new SpacedVector3(1, 2, 3), 10, 10, 10, new SpacedRotation(4, 5, 6, 7));
		String xml = xStream.toXML(cube);
		try {
			SpawnArea area = spawnAreaFactory.createArea(SpawnArea.class, xml);
			fail();
		} catch (SpawnAreaFactoryException e) {
			assertEquals("Unknown area type", e.getMessage());
		}
	}

	@Test
	public void testCreateAreaWithWrongXmlType() {
		Cube cube = new Cube(new SpacedVector3(1, 2, 3), 10, 10, 10, new SpacedRotation(4, 5, 6, 7));
		String xml = xStream.toXML(cube);
		try {
			SpawnArea area = spawnAreaFactory.createArea(SinglePointSpawnArea.class, xml);
			fail();
		} catch (SpawnAreaFactoryException e) {
			assertTrue(e.getMessage().contains("Wrong type of Geometry XML"));
		}
	}

	@Test
	public void testCreateAreaWitNullXml() {
		String xml = null;
		try {
			SpawnArea area = spawnAreaFactory.createArea(SinglePointSpawnArea.class, xml);
			fail();
		} catch (SpawnAreaFactoryException e) {
			assertEquals("Xml content is null", e.getMessage());
		}
	}

}
