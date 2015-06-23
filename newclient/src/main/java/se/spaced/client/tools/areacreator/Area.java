package se.spaced.client.tools.areacreator;

import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.area.PointSequence;
import se.spaced.shared.world.walkmesh.LocalSpaceConverter;

public interface Area extends PointSequence {
	String NEW_LINE = "\r\n";

	void addAreaPoint(AreaPoint areaPoint);

	void insertAreaPointAfter(AreaPoint afterPoint, AreaPoint areaPoint);

	String asGeometryString(LocalSpaceConverter localSpaceConverter);

	String asListValueString(LocalSpaceConverter localSpaceConverter);

	void remove(AreaPoint point);

	void clear();

	void addGeometry(Geometry geometry, LocalSpaceConverter localSpaceConverter);
}
