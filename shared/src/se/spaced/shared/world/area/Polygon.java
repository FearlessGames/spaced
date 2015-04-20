package se.spaced.shared.world.area;

import com.ardor3d.math.type.ReadOnlyVector2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import se.ardortech.math.AABox;
import se.ardortech.math.Box;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.Geometries;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class Polygon implements Geometry, PointSequence {
	private  final List<SpacedVector3> points;
	private final UUID id;

	public Polygon() {
		this(UUID.ZERO);
	}

	public Polygon(UUID id) {
		this(id, Lists.<SpacedVector3>newArrayList());
	}

	public Polygon(UUID id, Iterable<SpacedVector3> points) {
		this.points = Lists.newArrayList(points);
		this.id = id;
	}

	@Override
	public ImmutableList<AreaPoint> getAreaPoints() {
		return ImmutableList.copyOf(Iterables.transform(points, Geometries.VECTOR_TO_AREA_POINT));
	}

	public ImmutableList<SpacedVector3> getPoints() {
		return ImmutableList.copyOf(points);
	}

	public boolean add(SpacedVector3 point) {
		return points.add(point);
	}

	public int size() {
		return points.size();
	}

	public SpacedVector3 get(int i) {
		return points.get(i);
	}

	//stolen from  http://www.cs.princeton.edu/introcs/35purple/BoundingBox.java.html
	public boolean containsPoint(ReadOnlyVector2 point) {
		int crossings = 0;
		for (int i = 0; i < size() - 1; i++) {
			double slope = (points.get(i + 1).getX() - points.get(i).getX()) / (points.get(i + 1).getZ() - points.get(i).getZ());
			boolean cond1 = (points.get(i).getZ() <= point.getY()) && (point.getY() < points.get(i + 1).getZ());
			boolean cond2 = (points.get(i + 1).getZ() <= point.getY()) && (point.getY() < points.get(i).getZ());
			boolean cond3 = point.getX() < slope * (point.getY() - points.get(i).getZ()) + points.get(i).getX();
			if ((cond1 || cond2) && cond3) {
				crossings++;
			}
		}
		return (crossings % 2 != 0);
	}

	public Rectangle2D getBoundingRect() {
		int xmin = Integer.MAX_VALUE;
		int ymin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		int ymax = Integer.MIN_VALUE;
		for (SpacedVector3 polygonPoint : points) {
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

		return new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin);
	}

	public Box getBoundingBox() {
		if (points.size() < 2) {
			throw new IllegalStateException(String.format("Only %d points in polygon.", points.size()));
		}
		Box box = new AABox(points.get(0), points.get(1));
		for (SpacedVector3 point : points) {
			box.expand(point);
		}
		return box;
	}

	@LuaMethod(name = "GetId")
	public UUID getId() {
		return id;
	}

	public void close() {
		if (points.get(0).equals(Iterables.getLast(points))) {
			return;
		}
		points.add(points.get(0));
	}

	@Override
	public String toString() {
		return "Polygon{" +
				"id=" + id +
				'}';
	}
}
