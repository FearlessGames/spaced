package se.spaced.shared.world.area;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.hiflyer.paparazzo.algorithm.AStar;
import se.hiflyer.paparazzo.impl.Paths;
import se.hiflyer.paparazzo.interfaces.Path;

import java.util.List;

public class PathPlanner {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final PolygonGraph polygonGraph;
	private final AStar<Polygon> search;
	private static final double CLOSE_ENOUGH_DISTANCE_SQ = 0.5 * 0.5;

	public PathPlanner(PolygonGraph polygonGraph) {
		this.polygonGraph = polygonGraph;
		search = new AStar<Polygon>(new SpacedPolygonDistanceHeuristic(), polygonGraph, new SpacedPolygonDistance());
	}


	public SpacedVector3 getNextWayPoint(SpacedVector3 currentPos, SpacedVector3 targetPos, String context) {
		Polygon currentPolygon = polygonGraph.getPolygon(currentPos);
		Polygon targetPolygon = polygonGraph.getPolygon(targetPos);
		 log.debug("{} * Current polygon is {}, current position is {}. Target polygon is {}, target position is {}", context, currentPolygon, currentPos, targetPolygon, targetPos);

		if (currentPolygon.equals(targetPolygon)) {
			return targetPos;
		}
		Path<Polygon> path = search.search(currentPolygon, targetPolygon);
		if (path.equals(Paths.FAIL)) {
			log.info("Failed to find path from {} ({}) to {} ({})", currentPolygon, currentPos, targetPolygon, targetPos);
			return targetPos;
		}
		return extractWaypointFromPath(currentPos, targetPos, path);
	}

	public SpacedVector3 extractWaypointFromPath(
			SpacedVector3 currentPos,
			SpacedVector3 targetPos,
			Path<Polygon> path) {
		List<Gate> gates = Lists.newLinkedList();
		PeekingIterator<Polygon> iterator = Iterators.peekingIterator(path.iterator());
		while (iterator.hasNext()) {
			Polygon polygon = iterator.next();
			if (iterator.hasNext()) {
				Polygon nextPolygon = iterator.peek();
				Gate gate = polygonGraph.getGate(polygon, nextPolygon);
				gates.add(gate);
			}
		}
		gates.add(new Gate(targetPos, targetPos));

		return getNextWaypointFromGates(currentPos, gates, targetPos);
	}

	private SpacedVector3 getNextWaypointFromGates(SpacedVector3 currentPos, List<Gate> gates, SpacedVector3 targetPos) {
		SpacedVector3 nextWayPoint = Funnel.getNextWayPoint(currentPos, gates);
		Gate firstGate = gates.get(0);
		SpacedVector3 projectedWayPoint = VectorMath.projectOntoPlane(nextWayPoint.subtract(currentPos),
				currentPos,
				firstGate.getPoint1(),
				firstGate.getPoint2()).add(currentPos);
		SpacedVector3 intersection = VectorMath.findIntersection(firstGate.getPoint1(),
				firstGate.getPoint2(),
				currentPos,
				projectedWayPoint);

		if (intersection.isNaN() || intersection.isInfinite()) {
			return projectedWayPoint;
		}
		if (SpacedVector3.distanceSq(currentPos, intersection) < CLOSE_ENOUGH_DISTANCE_SQ) {
			gates.remove(0);
			if (gates.size() < 2) {
				return targetPos;
			}
			return getNextWaypointFromGates(intersection, gates, targetPos);
		}
		return intersection;
	}
}
