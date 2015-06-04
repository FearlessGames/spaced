package se.spaced.shared.world;

import com.google.common.base.Function;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;

public class Geometries {
	private Geometries() {
	}

	public static final Function<AreaPoint, SpacedVector3> AREA_POINT_TO_VECTOR = new Function<AreaPoint, SpacedVector3>() {
		@Override
		public SpacedVector3 apply(AreaPoint areaPoint) {
			return areaPoint.getPoint();
		}
	};

	public static final Function<SpacedVector3, AreaPoint> VECTOR_TO_AREA_POINT = new Function<SpacedVector3, AreaPoint>() {
		@Override
		public AreaPoint apply(SpacedVector3 spacedVector3) {
			return new AreaPoint(spacedVector3, SpacedRotation.IDENTITY);
		}
	};
}
