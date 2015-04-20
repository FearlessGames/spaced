package se.ardortech.math;

import com.ardor3d.scenegraph.Spatial;

public interface Shape3D {
	boolean isInside(SpacedVector3 point);
	boolean isInside(SpacedVector3 point, double margin);
	Spatial getDebugShape();

	/**
	 *
	 * @return negative values = inside shape, positive values = outside shape
	 */
	double distanceToEdge(SpacedVector3 point);
}
