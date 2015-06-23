package se.spaced.server.model.spawn.area;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.impl.inmemory.*;
import se.spaced.server.persistence.migrator.ServerXStreamUnmarshaller;
import se.spaced.shared.world.area.SinglePoint;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SpawnAreaUnmarshallingTest {

	private ServerXStreamUnmarshaller xStreamConverter;

	@Before
	public void setUp() throws Exception {
		xStreamConverter = new ServerXStreamUnmarshaller(new InMemorySpellDao(),
				new InMemoryItemTemplateDao(), new InMemoryLootTemplateDao(), new InMemoryCreatureTypeDao(),
				new InMemoryMobTemplateDao(), new InMemoryFactionDao(), new InMemoryBrainTemplateDao(),
				new InMemorySpawnPatternTemplateDao(), new InMemoryCooldownTemplateDao(),
				new InMemoryGraveyardTemplateDao(), new InMemoryCurrencyDao(), new InMemoryAuraDao());
	}

	@Test
	public void unmarshalSinglePoint() throws Exception {
		SinglePointSpawnArea singlePoint = (SinglePointSpawnArea) xStreamConverter.getXStream().fromXML(
				"\t\t<area class=\"se.spaced.server.model.spawn.area.SinglePointSpawnArea\">\n" +
						"\t\t\t<pk>357a96aa-3fc6-403d-84a8-9e88a1d046f4</pk>\n" +
						"\t\t\t<spawnPoint>\n" +
						"\t\t\t\t<point x=\"3462\" y=\"62.12\" z=\"2450\"/>\n" +
						"\t\t\t\t<rotation x=\"0.0\" y=\"-0.821\" z=\"0.0\" w=\"0.5708\"/>\n" +
						"\t\t\t</spawnPoint>\n" +
						"\t\t</area>");
		assertNotNull(singlePoint);
		assertEquals(UUID.fromString("357a96aa-3fc6-403d-84a8-9e88a1d046f4"), singlePoint.getPk());
		assertEquals(new SinglePoint(new SpacedVector3(3462, 62.12, 2450), new SpacedRotation(0.0, -0.821, 0.0, 0.5708)), singlePoint.getSpawnPoint());
	}

	@Test
	public void unmarshallPolygon() throws Exception {
		PolygonSpaceSpawnArea polygon = (PolygonSpaceSpawnArea) xStreamConverter.getXStream().fromXML(
				"<area class=\"se.spaced.server.model.spawn.area.PolygonSpaceSpawnArea\">\n" +
						"\t<pk>357a96aa-3fc6-403d-84a8-9e88a1d046f4</pk>" +
						"\t<polygon>\n" +
						"\t\t<points>\n" +
						"\t\t\t<vector3 x=\"100\" y=\"0\" z=\"100\"/>\n" +
						"\t\t\t<vector3 x=\"200\" y=\"1\" z=\"100\"/>\n" +
						"\t\t\t<vector3 x=\"100\" y=\"2\" z=\"200\"/>\n" +
						"\t\t</points>\n" +
						"\t</polygon>\n" +
						"\t<rotation x=\"0.0\" y=\"-0.821\" z=\"0.0\" w=\"0.5708\"/>\n" +
						"</area>");

		assertNotNull(polygon);
		assertEquals(UUID.fromString("357a96aa-3fc6-403d-84a8-9e88a1d046f4"), polygon.getPk());
		assertEquals(3, polygon.getPolygon().size());
		assertEquals(new SpacedRotation(0.0, -0.821, 0.0, 0.5708), polygon.getRotation());
	}

	@Test
	public void unmarshalSinglePointList() throws Exception {
		CompositeSpawnArea multipoint = (CompositeSpawnArea) xStreamConverter.getXStream().fromXML(
				"<area class=\"se.spaced.server.model.spawn.area.CompositeSpawnArea\">\n" +
						"\t<pk>357a96aa-3fc6-403d-84a8-9e88a1d046f4</pk>\n" +
						"\t<areas>\n" +
						"\t\t<point>\n" +
						"\t\t\t<point x=\"3462\" y=\"62.12\" z=\"2450\"/>\n" +
						"\t\t\t<rotation x=\"0.0\" y=\"-0.821\" z=\"0.0\" w=\"0.5708\"/>\n" +
						"\t\t</point>\n" +
						"\t\t<point>\n" +
						"\t\t\t<point x=\"4462\" y=\"62.12\" z=\"3450\"/>\n" +
						"\t\t\t<rotation x=\"0.0\" y=\"-0.821\" z=\"0.0\" w=\"0.5708\"/>\n" +
						"\t\t</point>\n" +
						"\t</areas>\n" +
						"</area>");

		assertNotNull(multipoint);
	}

	@Test
	public void unmarshalMultipoint() throws Exception {
		CompositeSpawnArea composite = (CompositeSpawnArea) xStreamConverter.getXStream().fromXML(
				"\t\t<area class=\"se.spaced.server.model.spawn.area.CompositeSpawnArea\">\n" +
						"\t\t\t<pk>8b7953ab-1f7b-483e-abcb-9fad65bb7ec7</pk>\n" +
						"\t\t\t<areas>\n" +
						"\t\t\t\t<pointArea>\n" +
						"\t\t\t\t\t<spawnPoint>\n" +
						"\t\t\t\t\t\t<point x=\"3215.696289\" y=\"9.902967\" z=\"2271.590576\"/>\n" +
						"\t\t\t\t\t\t<rotation x=\"0.0\" y=\"0.99804\" z=\"0.0\" w=\"-0.062548\"/>\n" +
						"\t\t\t\t\t</spawnPoint>\n" +
						"\t\t\t\t</pointArea>\n" +
						"\t\t\t\t<pointArea>\n" +
						"\t\t\t\t\t<spawnPoint>\n" +
						"\t\t\t\t\t\t<point x=\"2999.219482\" y=\"13.175127\" z=\"2377.328613\"/>\n" +
						"\t\t\t\t\t\t<rotation x=\"0.000000\" y=\"-0.733347\" z=\"0.000000\" w=\"0.679855\"/>\n" +
						"\t\t\t\t\t</spawnPoint>\n" +
						"\t\t\t\t</pointArea>\n" +
						"\n" +
						"\t\t\t\t<pointArea>\n" +
						"\t\t\t\t\t<spawnPoint>\n" +
						"\t\t\t\t\t\t<point x=\"3252.314453\" y=\"13.756456\" z=\"2367.623779\"/>\n" +
						"\t\t\t\t\t\t<rotation x=\"0.000000\" y=\"0.917452\" z=\"0.000000\" w=\"0.397846\"/>\n" +
						"\t\t\t\t\t</spawnPoint>\n" +
						"\t\t\t\t</pointArea>\n" +
						"\t\t\t</areas>\n" +
						"\n" +
						"\t\t</area>");

		assertNotNull(composite);
		composite.random = new Random();
		SpawnPoint nextSpawnPoint1 = composite.getNextSpawnPoint();
		assertNotNull(nextSpawnPoint1);
		nextSpawnPoint1.addSpawn();
		assertEquals(1, composite.getSpawnCount());

		SpawnPoint nextSpawnPoint2 = composite.getNextSpawnPoint();
		nextSpawnPoint2.addSpawn();
		assertEquals(2, composite.getSpawnCount());
	}
}
