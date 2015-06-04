package se.ardortech.math;

import com.ardor3d.math.type.ReadOnlyVector2;

public interface Shape2D {
	boolean isInside(ReadOnlyVector2 point);
}
