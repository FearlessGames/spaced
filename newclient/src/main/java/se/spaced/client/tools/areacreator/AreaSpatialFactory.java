package se.spaced.client.tools.areacreator;

import com.ardor3d.scenegraph.Spatial;
import se.spaced.shared.world.area.PointSequence;

public interface AreaSpatialFactory {
	Spatial create(PointSequence points);
}
