package se.spaced.client.ardor.terrain;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.world.terrain.AbstractHeightmapLoader;
import se.spaced.shared.world.terrain.HeightMap;
import se.spaced.shared.world.terrain.HeightmapLoader;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class BumpyGroundTest {

	private ThreadPoolExecutor executor;

	@Before
	public void setUp() throws Exception {
		executor = mock(ThreadPoolExecutor.class);
	}

	@Test
	public void testFlatWorldWithNoScale() throws IOException {
		HeightmapLoader loader = new AbstractHeightmapLoader("foo", 16, 1.0f, 1.0f) {
			@Override
			public HeightMap loadHeightMap() throws IOException {
				data = new double[size * size];
				for (int i = 0; i < data.length; i++) {
					data[i] = 10f;
				}
				return HeightMap.fromArray(size, size * widthScale, heightScale, data);
			}
		};

		BumpyGround ground = new BumpyGround(loader.loadHeightMap(), 5, executor);
		ground.init();

		float seaLevel = ground.getSeaLevelAdjustment();
		assertEquals(seaLevel + 10, ground.getHeight(SpacedVector3.ZERO), 0.1);
		assertEquals(seaLevel + 10, ground.getHeight(new SpacedVector3(16, 0, 16)), 0.1);
	}

	@Test
	public void testCorrectHeightAtDefinedPointsWithNoScale() throws IOException {
		HeightmapLoader loader = new AbstractHeightmapLoader("foo", 2, 1.0f, 1.0f) {

			@Override
			public HeightMap loadHeightMap() throws IOException {
				data = new double[size * size];
				data[0] = 10;
				data[1] = 20;
				data[2] = 10;
				data[3] = 30;
				return HeightMap.fromArray(size, widthScale, heightScale, data);
			}
		};

		BumpyGround ground = new BumpyGround(loader.loadHeightMap(), 5, executor);
		ground.init();

		float seaLevel = ground.getSeaLevelAdjustment();
		assertEquals(seaLevel + 10, ground.getHeight(SpacedVector3.ZERO), 0.1);
		assertEquals(seaLevel + 20, ground.getHeight(new SpacedVector3(1, 0, 0)), 0.1);
		assertEquals(seaLevel + 10, ground.getHeight(new SpacedVector3(0, 0, 1)), 0.1);
		assertEquals(seaLevel + 30, ground.getHeight(new SpacedVector3(1, 0, 1)), 0.1);
	}

	@Test
	public void testCorrectHeightBetweenPointsWithNoScale() throws IOException {
		HeightmapLoader loader = new AbstractHeightmapLoader("foo", 2, 1.0f, 1.0f) {

			@Override
			public HeightMap loadHeightMap() throws IOException {
				data = new double[size * size];
				data[0] = 10;
				data[1] = 20;
				data[2] = 10;
				data[3] = 30;
				return HeightMap.fromArray(size, widthScale, heightScale, data);
			}
		};

		BumpyGround ground = new BumpyGround(loader.loadHeightMap(), 5, executor);
		ground.init();

		float seaLevel = ground.getSeaLevelAdjustment();

		assertEquals(seaLevel + 15, ground.getHeight(new SpacedVector3(0.5, 0, 0)), 0.1);
		assertEquals(seaLevel + 20, ground.getHeight(new SpacedVector3(0.5, 0, 1.0)), 0.1);
		assertEquals(seaLevel + 10, ground.getHeight(new SpacedVector3(0, 0, 0.5)), 0.1);
		assertEquals(seaLevel + 25, ground.getHeight(new SpacedVector3(1, 0, 0.5)), 0.1);
	}

	@Test
	public void testCorrectHeightBetweenPointsWithHeightScale() throws IOException {
		HeightmapLoader loader = new AbstractHeightmapLoader("foo", 2, 1.0f, 2.0f) {

			@Override
			public HeightMap loadHeightMap() throws IOException {
				data = new double[size * size];
				data[0] = 10;
				data[1] = 20;
				data[2] = 10;
				data[3] = 30;
				return HeightMap.fromArray(size, widthScale, heightScale, data);
			}
		};

		BumpyGround ground = new BumpyGround(loader.loadHeightMap(), 5, executor);
		ground.init();

		float seaLevel = ground.getSeaLevelAdjustment();

		assertEquals(seaLevel + 30, ground.getHeight(new SpacedVector3(0.5, 0, 0)), 0.1);
		assertEquals(seaLevel + 40, ground.getHeight(new SpacedVector3(0.5, 0, 1.0)), 0.1);
		assertEquals(seaLevel + 20, ground.getHeight(new SpacedVector3(0, 0, 0.5)), 0.1);
		assertEquals(seaLevel + 50, ground.getHeight(new SpacedVector3(1, 0, 0.5)), 0.1);
	}

	@Test
	public void testCorrectHeightBetweenPointsWithWidthScale() throws IOException {
		HeightmapLoader loader = new AbstractHeightmapLoader("foo", 2, 2.0f, 1.0f) {

			@Override
			public HeightMap loadHeightMap() throws IOException {
				data = new double[size * size];
				data[0] = 10;
				data[1] = 20;
				data[2] = 10;
				data[3] = 30;
				return HeightMap.fromArray(size, widthScale, heightScale, data);
			}
		};

		BumpyGround ground = new BumpyGround(loader.loadHeightMap(), 5, executor);
		ground.init();

		float seaLevel = ground.getSeaLevelAdjustment();

		assertEquals(seaLevel + 15, ground.getHeight(new SpacedVector3(1, 0, 0)), 0.1);
		assertEquals(seaLevel + 20, ground.getHeight(new SpacedVector3(1, 0, 2.0)), 0.1);
		assertEquals(seaLevel + 10, ground.getHeight(new SpacedVector3(0, 0, 1)), 0.1);
		assertEquals(seaLevel + 25, ground.getHeight(new SpacedVector3(2, 0, 1)), 0.1);
	}


}
