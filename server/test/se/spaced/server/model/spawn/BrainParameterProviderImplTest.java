package se.spaced.server.model.spawn;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.shared.util.random.RealRandomProvider;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.area.Path;
import se.spaced.shared.world.area.SinglePoint;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static se.mockachino.Mockachino.*;

public class BrainParameterProviderImplTest {

	private MobSpawnTemplate mobSpawnTemplate;
	private BrainParameterProvider brainParameterProvider;
	private MobTemplate mobTemplate;


	@Before
	public void setUp() throws Exception {
		mobSpawnTemplate = mock(MobSpawnTemplate.class);
		mobTemplate = mock(MobTemplate.class);
		brainParameterProvider = new BrainParameterProviderImpl(mobSpawnTemplate,
				new RealRandomProvider(new Random(4711)),
				mobTemplate);
	}

	@Test
	public void getRoamAreaFromMobSpawnTemplateGeometry() throws Exception {
		SpacedVector3 point = new SpacedVector3(10, 20, 30);
		Geometry geometry = new SinglePoint(point, SpacedRotation.IDENTITY);
		when(mobSpawnTemplate.getGeometry()).thenReturn(geometry);
		SpawnArea roamArea = brainParameterProvider.getRoamArea();

		assertEquals(point, roamArea.getNextSpawnPoint().getPosition());
	}

	@Test
	public void getRoamAreaFromMobSpawnTemplateRoamArea() throws Exception {
		SpacedVector3 point = new SpacedVector3(10, 20, 30);
		Geometry geometry = new SinglePoint(point, SpacedRotation.IDENTITY);
		when(mobSpawnTemplate.getGeometry()).thenReturn(geometry);

		SpacedVector3 point2 = new SpacedVector3(31, 21, 11);
		Geometry geometry2 = new SinglePoint(point2, SpacedRotation.IDENTITY);
		when(mobSpawnTemplate.getRoamArea()).thenReturn(geometry2);

		SpawnArea roamArea = brainParameterProvider.getRoamArea();

		assertEquals(point2, roamArea.getNextSpawnPoint().getPosition());
	}

	@Test
	public void getScriptPath() throws Exception {
		String path = "some/path";
		when(mobTemplate.getScriptPath()).thenReturn(path);
		String scriptPath = brainParameterProvider.getScriptPath();
		assertEquals(path, scriptPath);
	}

	@Test
	public void getScriptPathWhenItIsNull() throws Exception {
		try {
			String scriptPath = brainParameterProvider.getScriptPath();
			fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void getScriptPathWhenItIsEmpty() throws Exception {
		try {
			when(mobTemplate.getScriptPath()).thenReturn("");
			String scriptPath = brainParameterProvider.getScriptPath();
			fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void getPatrolPath() throws Exception {
		List<SpacedVector3> points = Lists.newArrayList();
		points.add(new SpacedVector3(10, 20, 30));
		points.add(new SpacedVector3(11, 22, 33));
		Path path = new Path(points);
		when(mobSpawnTemplate.getGeometry()).thenReturn(path);
		Path patrolPath = brainParameterProvider.getPatrolPath();
		assertEquals(path.getPathPoints(), patrolPath.getPathPoints());
	}

	@Test
	public void failToGetPatrolPath() throws Exception {
		try {
			Path patrolPath = brainParameterProvider.getPatrolPath();
			fail();
		} catch (Exception e) {

		}
	}
}
