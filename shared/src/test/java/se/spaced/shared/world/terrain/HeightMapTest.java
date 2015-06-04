package se.spaced.shared.world.terrain;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import org.junit.Test;
import se.fearlessgames.common.publisher.Subscriber;
import se.krka.kahlua.integration.expose.ReturnValues;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class HeightMapTest {

	private static final double EPSILON = 1e-6;

	@Test
	public void fromArrayAtEdges() throws Exception {
		double[] data = new double[] {1.0, 2.0, 1.5, 0.5};
		HeightMap map = HeightMap.fromArray(2, 1, 10, data);
		assertEquals(5.0f, map.getHeight(1, 1), EPSILON);
		assertEquals(20.0f, map.getHeight(1, 0), EPSILON);
		assertEquals(15.0f, map.getHeight(0, 1), EPSILON);
		assertEquals(10.0f, map.getHeight(0, 0), EPSILON);
	}

	@Test
	public void fromArrayAtDataPoints() throws Exception {
		double[] data = new double[] {1.0, 2.0, 1.5, 0.5};
		HeightMap map = HeightMap.fromArray(2, 10, 10, data);
		assertEquals(5.0f, map.getHeight(7.5, 7.5), EPSILON);
		assertEquals(10.0f, map.getHeight(2.5, 2.5), EPSILON);
		assertEquals(20.0f, map.getHeight(7.5, 2.5), EPSILON);
		assertEquals(15.0f, map.getHeight(2.5, 7.5), EPSILON);
	}


	@Test
	public void fromArrayBiggerWithWidthScale() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
												1.0, 1.2, 1.6, 3.0,
												0.8, 1.0, 1.3, 2.5,
												0.5, 1.0, 1.2, 2.2};
		HeightMap map = HeightMap.fromArray(4, 10, 10, data);

		assertEquals(10.0, map.getHeight(0, 0), EPSILON);
		assertEquals(40.0, map.getHeight(10, 0), EPSILON);
		assertEquals(5.0, map.getHeight(0, 10), EPSILON);
		assertEquals(22.0, map.getHeight(10, 10), EPSILON);

		assertEquals(10.0, map.getHeight(1.25, 1.25), EPSILON);
		assertEquals(40.0, map.getHeight(8.75, 1.25), EPSILON);
		assertEquals(5.0, map.getHeight(1.25, 8.75), EPSILON);
		assertEquals(22.0, map.getHeight(8.75, 8.75), EPSILON);
	}

	@Test
	public void fromHeightMap() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
												1.0, 1.2, 1.6, 3.0,
												0.8, 1.0, 1.3, 2.5,
												0.5, 1.0, 1.2, 2.2};
		HeightMap base = HeightMap.fromArray(4, 10, 10, data);
		HeightMap map = HeightMap.fromHeightMap(base, 2);

		assertEquals(11.23, map.getHeight(0, 0), EPSILON);
		assertEquals(24.26, map.getHeight(10, 0), EPSILON);
		assertEquals(7.97, map.getHeight(0, 10), EPSILON);
		assertEquals(16.12, map.getHeight(10, 10), EPSILON);

		assertEquals(2, map.getSize());
	}

	@Test(expected = IllegalArgumentException.class)
	public void powerOfTwoRequired() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0,
												1.0, 1.2, 1.6,
												0.8, 1.0, 1.3,
												0.5, 1.0, 1.2};
		HeightMap base = HeightMap.fromArray(3, 10, 10, data);
	}

	@Test(expected = IllegalStateException.class)
	public void dataMismatch() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0,
				1.0, 1.2, 1.6,
				0.8, 1.0, 1.3,
				0.5, 1.0, 1.2};
		HeightMap base = HeightMap.fromArray(4, 10, 10, data);
	}

	@Test
	public void updateData() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
				1.0, 1.2, 1.6, 3.0,
				0.8, 1.0, 1.3, 2.5,
				0.5, 1.0, 1.2, 2.2};
		HeightMap map = HeightMap.fromArray(4, 10, 10, data);

		map.update(1.25, 1.25, 10.0);

		assertEquals(20.0, map.getHeight(1.25, 1.25), EPSILON);
		assertEquals(40.0, map.getHeight(8.75, 1.25), EPSILON);
		assertEquals(5.0, map.getHeight(1.25, 8.75), EPSILON);
		assertEquals(22.0, map.getHeight(8.75, 8.75), EPSILON);

		assertEquals(20.0, map.getHeight(0, 0), EPSILON);
		assertEquals(40.0, map.getHeight(10, 0), EPSILON);
		assertEquals(5.0, map.getHeight(0, 10), EPSILON);
		assertEquals(22.0, map.getHeight(10, 10), EPSILON);
	}

	@Test
	public void updateDataBetweenDataPoints() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
												1.0, 1.2, 1.6, 3.0,
												0.8, 1.0, 1.3, 2.5,
												0.5, 1.0, 1.2, 2.2};
		HeightMap map = HeightMap.fromArray(4, 10, 10, data);

		map.update(2.5, 2.5, 10.0);

		// 1.25  3.75  6.25  8.75
		assertEquals(15.0, map.getHeight(1.25, 1.25), EPSILON);
		assertEquals(20.0, map.getHeight(3.75, 1.25), EPSILON);
		assertEquals(15.0, map.getHeight(1.25, 3.75), EPSILON);
		assertEquals(17.0, map.getHeight(3.75, 3.75), EPSILON);


		assertEquals(40.0, map.getHeight(8.75, 1.25), EPSILON);
		assertEquals(5.0, map.getHeight(1.25, 8.75), EPSILON);
		assertEquals(22.0, map.getHeight(8.75, 8.75), EPSILON);
	}


	@Test
	public void updateChainedData() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
				1.0, 1.2, 1.6, 3.0,
				0.8, 1.0, 1.3, 2.5,
				0.5, 1.0, 1.2, 2.2};
		HeightMap base = HeightMap.fromArray(4, 10, 10, data);
		HeightMap map= HeightMap.fromHeightMap(base, 2);

		double preHeight1 = map.getHeight(1.25, 1.25);
		double preHeight2 = map.getHeight(8.75, 8.75);
		base.update(1.25, 1.25, 10.0);
		double postHeight1 = map.getHeight(1.25, 1.25);
		double postHeight2 = map.getHeight(8.75, 8.75);
		assertTrue(preHeight1 < postHeight1);
		assertTrue(preHeight2 < postHeight2);
		assertTrue(postHeight1 - preHeight1 > postHeight2 - preHeight2);

	}

	@Test
	public void updateDataNotifiesSubscriber() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
				1.0, 1.2, 1.6, 3.0,
				0.8, 1.0, 1.3, 2.5,
				0.5, 1.0, 1.2, 2.2};
		HeightMap map = HeightMap.fromArray(4, 10, 10, data);

		Subscriber<HeightMap> subsriber = mock(Subscriber.class);
		map.subscribe(subsriber);

		map.update(1.0, 1.0, 1.0);

		verifyOnce().on(subsriber).update(map);
	}

	@Test
	public void verifyIndices() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
				1.0, 1.2, 1.6, 3.0,
				0.8, 1.0, 1.3, 2.5,
				0.5, 1.0, 1.2, 2.2};
		HeightMap map = HeightMap.fromArray(4, 8, 10, data);
		ArrayTable<Integer,Integer,DataPoint> table = map.getHeightData();
		for (int x = 0; x < 4; x++) {
			for (int z = 0; z < 4; z++) {
				DataPoint dataPoint = table.at(x, z);
				assertEquals(x, dataPoint.getXIndex());
				assertEquals(z, dataPoint.getZIndex());
			}
		}
	}


	@Test
	public void retrieveAllCloseNodesSimple() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
				1.0, 1.2, 1.6, 3.0,
				0.8, 1.0, 1.3, 2.5,
				0.5, 1.0, 1.2, 2.2};
		HeightMap map = HeightMap.fromArray(4, 8, 10, data);

		Collection<DataPoint> dataPoints = Lists.newArrayList(map.getDataPointsNearby(3.0f, 3.0f, 0.5f));
		// 1 3 5 7
		assertEquals(1, dataPoints.size());
		DataPoint point = Iterables.getOnlyElement(dataPoints);
		assertEquals(1, point.getXIndex());
		assertEquals(1, point.getZIndex());
		ReturnValues indices = mock(ReturnValues.class);
		point.getIndices(indices);
		verifyExactly(2).on(indices).push(1);
	}

	@Test
	public void retrieveAllCloseNodesCornerSmall() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
				1.0, 1.2, 1.6, 3.0,
				0.8, 1.0, 1.3, 2.5,
				0.5, 1.0, 1.2, 2.2};
		HeightMap map = HeightMap.fromArray(4, 8, 10, data);

		Collection<DataPoint> dataPoints = Lists.newArrayList(map.getDataPointsNearby(0.0f, 0.0f, 2.0f));
		// 1 3 5 7
		assertEquals(1, dataPoints.size());
		assertNotNull(Iterables.find(dataPoints, new Predicate<DataPoint>() {
			@Override
			public boolean apply(DataPoint dataPoint) {
				return Doubles.compare(dataPoint.getX(), 1) == 0 && Doubles.compare(dataPoint.getZ(), 1) == 0;
			}
		}));
	}

	@Test
	public void retrieveAllCloseNodesCornerLarge() throws Exception {
		double[] data = new double[] {1.0, 1.5, 2.0, 4.0,
				1.0, 1.2, 1.6, 3.0,
				0.8, 1.0, 1.3, 2.5,
				0.5, 1.0, 1.2, 2.2};
		HeightMap map = HeightMap.fromArray(4, 8, 10, data);

		Collection<DataPoint> dataPoints = Lists.newArrayList(map.getDataPointsNearby(0.0f, 0.0f, 4.24f));
		// 1 3 5 7
		assertEquals(3, dataPoints.size());
		assertNotNull(Iterables.find(dataPoints, new Predicate<DataPoint>() {
			@Override
			public boolean apply(DataPoint dataPoint) {
				return Doubles.compare(dataPoint.getX(), 1) == 0 && Doubles.compare(dataPoint.getZ(), 1) == 0;
			}
		}));

		dataPoints = Lists.newArrayList(map.getDataPointsNearby(0.0f, 0.0f, 4.25f));
		// 1 3 5 7
		assertEquals(4, dataPoints.size());
		assertNotNull(Iterables.find(dataPoints, new Predicate<DataPoint>() {
			@Override
			public boolean apply(DataPoint dataPoint) {
				return Doubles.compare(dataPoint.getX(), 3) == 0 && Doubles.compare(dataPoint.getZ(), 3) == 0;
			}
		}));

	}
}
