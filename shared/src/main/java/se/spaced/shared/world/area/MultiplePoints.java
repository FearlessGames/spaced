package se.spaced.shared.world.area;

import java.util.ArrayList;
import java.util.List;

public class MultiplePoints implements Geometry {
	private final List<SinglePoint> points;

	public MultiplePoints() {
		points = new ArrayList<SinglePoint>();
	}

	public List<SinglePoint> getPoints() {
		return points;
	}

	public void add(SinglePoint singlePoint) {
		points.add(singlePoint);
	}
}
