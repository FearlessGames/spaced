package se.spaced.server.model.spawn.area;

import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyVector2;
import org.hibernate.annotations.Type;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;
import se.spaced.shared.world.area.Polygon;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class PolygonSpaceSpawnArea extends ExternalPersistableBase implements SpawnArea {

	@Type(type = "xml")
	private Polygon polygon;

	private SpacedRotation rotation;

	@Transient
	private BoundingBox boundingBox;

	@Transient
	private RandomProvider randomProvider = new RealRandomProvider();

	@Transient
	private final AtomicInteger spawnCount = new AtomicInteger(0);

	protected PolygonSpaceSpawnArea() {
	}

	public PolygonSpaceSpawnArea(UUID pk, Polygon polygon, SpacedRotation rotation, RandomProvider randomProvider) {
		super(pk);
		this.polygon = polygon;
		this.polygon.add(this.polygon.getPoints().get(0));
		this.rotation = rotation;
		this.randomProvider = randomProvider;
		boundingBox = new BoundingBox(polygon);
	}



	@Override
	public SpawnPoint getNextSpawnPoint() {
		if (boundingBox == null) {
			boundingBox = new BoundingBox(polygon);
		}
		ReadOnlyVector2 point;
		do {
			point = boundingBox.getRandomPoint();
		} while (!polygon.containsPoint(point));

		return new SpawnPoint(this, new SpacedVector3(point.getX(), polygon.getPoints().get(0).getY(), point.getY()), rotation);
	}

	@Override
	public void addSpawn() {
		spawnCount.incrementAndGet();
	}

	@Override
	public void removeSpawn() {
		spawnCount.decrementAndGet();
	}

	@Override
	public int getSpawnCount() {
		return spawnCount.get();
	}


	private class BoundingBox {
		private IntervalInt intervalX;
		private IntervalInt intervalY;

		private BoundingBox(Polygon polygon) {
			int xmin = Integer.MAX_VALUE;
			int ymin = Integer.MAX_VALUE;
			int xmax = Integer.MIN_VALUE;
			int ymax = Integer.MIN_VALUE;
			for (SpacedVector3 polygonPoint : polygon.getPoints()) {
				if (polygonPoint.getX() < xmin) {
					xmin = (int) polygonPoint.getX();
				}
				if (polygonPoint.getZ() < ymin) {
					ymin = (int) polygonPoint.getZ();
				}
				if (polygonPoint.getX() > xmax) {
					xmax = (int) polygonPoint.getX();
				}
				if (polygonPoint.getZ() > ymax) {
					ymax = (int) polygonPoint.getZ();
				}
			}

			intervalX = new IntervalInt(xmin, xmax);
			intervalY = new IntervalInt(ymin, ymax);
		}

		private ReadOnlyVector2 getRandomPoint() {
			return new Vector2(randomProvider.getInteger(intervalX), randomProvider.getInteger(intervalY));
		}

	}

	public Polygon getPolygon() {
		return polygon;
	}

	public SpacedRotation getRotation() {
		return rotation;
	}
}
