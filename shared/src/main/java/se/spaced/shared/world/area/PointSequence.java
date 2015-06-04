package se.spaced.shared.world.area;

import com.google.common.collect.ImmutableList;
import se.spaced.shared.world.AreaPoint;

public interface PointSequence {
	ImmutableList<AreaPoint> getAreaPoints();
}
