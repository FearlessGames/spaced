package se.spaced.shared.world.area;

import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyVector2;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.fearless.common.uuid.UUID;
import se.hiflyer.paparazzo.interfaces.NeighbourLookup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class PolygonGraph implements NeighbourLookup<Polygon> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final Polygon EVERYWHERE = new Polygon(UUID.ZERO, Arrays.asList(
			new SpacedVector3(-Double.MAX_VALUE, 0, -Double.MAX_VALUE),
			new SpacedVector3(Double.MAX_VALUE, 0, -Double.MAX_VALUE),
			new SpacedVector3(Double.MIN_VALUE, 0, Double.MAX_VALUE),
			new SpacedVector3(-Double.MAX_VALUE, 0, Double.MAX_VALUE))) {
		@Override
		public String toString() {
			return "EVERYWHERE";
		}
	};
	private static final double GATE_INSET = 0.7;

	private final Multimap<Polygon, Neighbour> polygons = HashMultimap.create();

	RTree<Polygon, Polygon> polygonLookup = RTree.star().create();

	public void addPolygon(Polygon polygon) {
		polygons.put(polygon, new Neighbour(SpacedVector3.ZERO, SpacedVector3.ZERO, polygon));
		polygonLookup = polygonLookup.add(polygon, polygon);
	}

	public Collection<Polygon> getAllPolygons() {
		return polygons.keySet();
	}

	public void addNaturalConnection(Polygon from, Polygon to) {
		Set<SpacedVector3> pointsInCommon = Sets.newHashSet(from.getPoints());
		pointsInCommon.retainAll(to.getPoints());
		if (pointsInCommon.size() != 2) {
			throw new IllegalArgumentException(String.format("Polygons have %s points in common, should be 2", pointsInCommon.size()));
		}
		addConnection(from, to, Iterables.get(pointsInCommon, 0), Iterables.get(pointsInCommon, 1));
	}

	public void addConnection(Polygon from, Polygon to, SpacedVector3 point1, SpacedVector3 point2) {
		Neighbour neighbour = new Neighbour(point1, point2, to);
		polygons.put(from, neighbour);
	}

	@Override
	public Iterable<Polygon> getNeighbours(final Polygon polygon) {
		return polygons.get(polygon).stream().filter(neighbour -> !neighbour.polygon.equals(polygon)).map(neighbour -> neighbour.polygon).collect(Collectors.toList());
	}

	public Polygon getPolygon(SpacedVector3 point) {
		final ReadOnlyVector2 vector2 = new Vector2(point.getX(), point.getZ());
		Observable<Entry<Polygon, Polygon>> search = polygonLookup.search(Geometries.point(vector2.getX(), vector2.getY()));
		Iterable<Polygon> inside = search.map(Entry::value).filter(polygon -> polygon.containsPoint(vector2)).toBlocking().toIterable();

		Polygon closestDownwards = EVERYWHERE;
		double closestDistance = Double.MAX_VALUE;
		for (Polygon polygon : inside) {
			double distance = getDistanceToPolygon(point, polygon);
			log.debug("Distance from {} to polygon {} is {}", point, polygon, distance);
			if (distance < 0) {
				distance = Math.abs(distance) * 5;
			}
			if (distance < closestDistance) {
				closestDownwards = polygon;
				closestDistance = distance;
			}
		}
		return closestDownwards;
	}

	public Polygon getPolygon(final UUID id) {
		return polygons.keySet().stream().filter(polygon -> polygon.getId().equals(id)).findFirst().get();
	}

	private double getDistanceToPolygon(SpacedVector3 point, Polygon polygon) {
		ImmutableList<SpacedVector3> points = polygon.getPoints();
		Iterator<SpacedVector3> pointIterator = points.iterator();
		SpacedVector3 p1 = pointIterator.next();
		SpacedVector3 p2 = pointIterator.next();
		SpacedVector3 p3 = pointIterator.next();

		SpacedVector3 normal = VectorMath.getNormal(p1, p2, p3);
		if (normal.getY() < 0) {
			normal = normal.negate();
		}
		log.debug("Normal {}", normal);
		return point.subtract(p1).dot(normal);
	}

	public void addBidirectionalConnection(Polygon p1, Polygon p2) {
		addNaturalConnection(p1, p2);
		addNaturalConnection(p2, p1);
	}

	public Gate getGate(Polygon from, final Polygon to) {
		Collection<Neighbour> neighbours = polygons.get(from);
		Neighbour neighbour = neighbours.stream().filter(neighbour1 -> to.equals(neighbour1.polygon)).findFirst().get();
		return neighbour.gate;
	}

	private static class Neighbour {
		final Polygon polygon;
		final Gate gate;

		private Neighbour(SpacedVector3 point1, SpacedVector3 point2, Polygon polygon) {
			SpacedVector3 diff = point2.subtract(point1).normalize().scalarMultiply(GATE_INSET);

			gate = new Gate(point1.add(diff), point2.subtract(diff));
			this.polygon = polygon;
		}
	}
}
