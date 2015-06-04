package se.ardortech.math;

import com.ardor3d.math.type.ReadOnlyVector2;

public interface Rectangle extends Shape2D {
	ReadOnlyVector2 getCenter();
	ReadOnlyVector2 getSize();
	ReadOnlyVector2 getMax();
	ReadOnlyVector2 getMin();
	void set(Rectangle rectangle);
	void setMinMax(ReadOnlyVector2 min, ReadOnlyVector2 max);
	void setCenterSize(ReadOnlyVector2 center, ReadOnlyVector2 size);
	boolean overlap(Rectangle rectangle);
	void expand(ReadOnlyVector2 vector);
	void translate(ReadOnlyVector2 vector);
}