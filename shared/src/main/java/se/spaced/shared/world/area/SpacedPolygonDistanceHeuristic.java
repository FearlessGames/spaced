package se.spaced.shared.world.area;

import com.ardor3d.math.Vector2;
import se.hiflyer.paparazzo.interfaces.HeuristicEstimator;

import java.awt.geom.Rectangle2D;

public class SpacedPolygonDistanceHeuristic implements HeuristicEstimator<Polygon> {
	@Override
	public double estimate(Polygon polygon1, Polygon polygon2) {
		Rectangle2D rect1 = polygon1.getBoundingRect();
		Rectangle2D rect2 = polygon2.getBoundingRect();
		return new Vector2(rect1.getCenterX(), rect1.getCenterY()).distance(rect2.getCenterX(), rect2.getCenterY());
	}
}
