package se.spaced.shared.world.terrain;

import com.ardor3d.math.MathUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import se.fearlessgames.common.publisher.Publisher;
import se.fearlessgames.common.publisher.SimplePublisher;
import se.fearlessgames.common.publisher.Subscriber;

public class HeightMap implements Publisher<HeightMap> {
	private final int size;
	private final ArrayTable<Integer, Integer, DataPoint> heightData;

	private final int mod;
	private final double heightScale;
	private final double invertedWidth;
	private final double widthScale;
	private final double mapWidth;
	private final double invertedSize;

	private final SimplePublisher<HeightMap> publisher = new SimplePublisher<HeightMap>();

	private HeightMap(int sampleSize, double mapWidth, double heightScale, double[] heightData) {
		this.mapWidth = mapWidth;
		if (!isPowerOfTwo(sampleSize)) {
			throw new IllegalArgumentException("sampleSize must be a power of two but was " + sampleSize);
		}
		if (sampleSize * sampleSize != heightData.length) {
			throw new IllegalStateException(String.format("Data mismatch. size is %d but heightdata length is %d", sampleSize, heightData.length));
		}

		this.size = sampleSize;
		this.mod = sampleSize - 1;
		Range<Integer> rowRange = Range.closed(0, mod);
		Range<Integer> colRange = Range.closed(0, mod);

		this.heightData = ArrayTable.create(ContiguousSet.create(rowRange, DiscreteDomain.integers()), ContiguousSet.create(colRange, DiscreteDomain.integers()));

		updateFromArray(heightData);


		this.widthScale = mapWidth / sampleSize;
		this.invertedWidth = 1 / mapWidth;
		this.invertedSize = 1.0 / sampleSize;
		this.heightScale = heightScale;
	}

	private void updateFromArray(double[] heightData) {
		double xStep = 0.5 * this.mapWidth / size;
		double zStep = 0.5 * this.mapWidth / size;
		for (int i = 0; i < heightData.length; i++) {
			int x = i % size;
			int z = i / size;
			this.heightData.set(x, z, new DataPoint(x, z, (2* x + 1) * xStep, (2 * z + 1) * zStep, heightData[i]));
		}
	}

	public static HeightMap fromArray(int sampleSize, double mapWidth, double heightScale, double[] heightData) {
		return new HeightMap(sampleSize, mapWidth, heightScale, heightData);
	}

	public int getSize() {
		return size;
	}

	public ArrayTable<Integer, Integer, DataPoint> getHeightData() {
		return heightData;
	}

	private boolean isPowerOfTwo(int n) {
		return (n & (n - 1)) == 0;
	}

	public double  getHeight(final double x, final double z) {
		return getRawHeight(x, z)* heightScale;
	}


	public double getHeightScale() {
		return heightScale;
	}

	public double getWidthScale() {
		return widthScale;
	}

	public static HeightMap fromHeightMap(final HeightMap base, final int scale) {
		double[] newData = createNewData(base, scale);
		final HeightMap subMap = new HeightMap(base.getSize() / scale, base.mapWidth, base.getHeightScale(), newData);
		base.subscribe(new Subscriber<HeightMap>() {
			@Override
			public void update(HeightMap updated) {
				subMap.updateHeightData(createNewData(base, scale));
			}
		});
		return subMap;
	}

	private void updateHeightData(double[] newData) {
		updateFromArray(newData);
		publisher.updateSubscribers(this);
	}

	private static double[] createNewData(HeightMap base, int scale) {
		int newSize = base.getSize() / scale;
		double step = base.getWidthScale() * scale;
		double[] newData = new double[newSize * newSize];
		for (int y = 0; y < newSize; y++) {
			for (int x = 0; x < newSize; x++) {
				double xCoordinate = x * step + base.getWidthScale();
				double yCoordinate = y * step + base.getWidthScale();
				double height = base.getRawHeight((int) xCoordinate, (int) yCoordinate);
				newData[x + y * newSize] = height;
			}
		}
		return newData;
	}

	public double getRawHeight(double x, double z) {
		PointData pointData = new PointData(x, z);
		int col1 = clampCol(pointData.getCol1());
		int row1 = clampRow(pointData.getRow1());
		int col2 = clampCol(pointData.getCol2());
		int row2 = clampRow(pointData.getRow2());
		double intOnX = pointData.getIntOnX();
		double intOnZ = pointData.getIntOnZ();

		// find the heightmap point closest to this position (but will always
		// be to the left ( < x) and above (< z) of the spot.
		final double topLeft = heightData.at(col1, row1).getHeight();

		// now find the next point to the right of topLeft's position...
		final double topRight = heightData.at(col2, row1).getHeight();

		// now find the next point below topLeft's position...
		final double bottomLeft = heightData.at(col1, row2).getHeight();

		// now find the next point below and to the right of topLeft's
		// position...
		final double bottomRight = heightData.at(col2, row2).getHeight();

		// Use linear interpolation to find the height.
		double topLerp = MathUtils.lerp(intOnX, topLeft, topRight);
		double bottomLerp = MathUtils.lerp(intOnX, bottomLeft, bottomRight);
		return MathUtils.lerp(intOnZ, topLerp, bottomLerp);
	}

	private int clampRow(int row1) {
		return Math.max(Math.min(row1, heightData.rowMap().size()), 0);
	}

	private int clampCol(int col1) {
		return Math.max(Math.min(col1, heightData.columnMap().size()), 0);
	}

	@Override
	public void subscribe(Subscriber<HeightMap> heightMapSubscriber) {
		publisher.subscribe(heightMapSubscriber);
	}

	@Override
	public void unsubscribe(Subscriber<HeightMap> heightMapSubscriber) {
		publisher.unsubscribe(heightMapSubscriber);
	}

	public void update(double x, double z, double dy) {
		PointData pointData = new PointData(x, z);
		int col1 = pointData.getCol1();
		int row1 = pointData.getRow1();
		int col2 = pointData.getCol2();
		int row2 = pointData.getRow2();
		double intOnX = pointData.getIntOnX();
		double intOnZ = pointData.getIntOnZ();

		double dy11 = ((1 - intOnX)  + (1 - intOnZ)) * dy * 0.5;
		updateSinglePoint(col1, row1, dy11);

		double dy12 = ((1 - intOnX)  + (intOnZ)) * dy * 0.5;
		updateSinglePoint(col1, row2, dy12);

		double dy21 = ((intOnX)  + (1 - intOnZ)) * dy * 0.5;
		updateSinglePoint(col2, row1, dy21);

		double dy22 = ((intOnX)  + (intOnZ)) * dy * 0.5;
		updateSinglePoint(col2, row2, dy22);

		publisher.updateSubscribers(this);
	}

	public void updateSinglePoint(int col, int row, double delta) {
		DataPoint old11 = heightData.at(col, row);
		heightData.set(col, row, new DataPoint(col, row, old11.getX(), old11.getZ(), old11.getHeight() + (delta / heightScale)));
	}

	public Iterable<DataPoint> getDataPointsNearby(final double x, final double z, double radius) {
		final double radiusSq = radius * radius;
		return Iterables.filter(heightData.values(), new DataPointInRangePredicate(x, z, radiusSq));
	}

	private static class DataPointInRangePredicate implements Predicate<DataPoint> {
		private final double x;
		private final double z;
		private final double radiusSq;

		DataPointInRangePredicate(double x, double z, double radiusSq) {
			this.x = x;
			this.z = z;
			this.radiusSq = radiusSq;
		}

		@Override
		public boolean apply(DataPoint dataPoint) {
			return dataPoint.inRange(x, z, radiusSq);
		}
	}

	private class PointData {
		private final int col1;
		private final int col2;
		private final int row1;
		private final int row2;
		private final double intOnX;
		private final double intOnZ;
		private final double dx;
		private final double dy;


		PointData(double x, double z) {
			double scaledX = x * invertedWidth;
			double scaledZ = z * invertedWidth;

			col1 = Math.max(0, (int) Math.floor(scaledX * mod));
			col2 = Math.min(mod, (int) Math.ceil(scaledX * mod));

			row1 = Math.max(0, (int) Math.floor(scaledZ * mod));
			row2 = Math.min(mod, (int) Math.ceil(scaledZ * mod));

			double left = ((double) col1 + 0.5) * invertedSize;
			double right = ((double) col2 + 0.5) * invertedSize;
			double top = ((double) row1 + 0.5) * invertedSize;
			double bottom = ((double) row2 + 0.5) * invertedSize;
			dx = right - left;
			dy = bottom - top;
			intOnX = (scaledX - left) / dx;
			intOnZ = (scaledZ - top) / dy;
		}

		public int getCol1() {
			return col1;
		}

		public int getCol2() {
			return col2;
		}

		public int getRow1() {
			return row1;
		}

		public int getRow2() {
			return row2;
		}

		public double getIntOnX() {
			return intOnX;
		}

		public double getIntOnZ() {
			return intOnZ;
		}
	}
}
