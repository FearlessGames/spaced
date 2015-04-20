package se.ardortech.math;

public interface Box extends Shape3D {
	SpacedVector3 getCenter();
	SpacedVector3 getSize();
	SpacedVector3 getMin();
	SpacedVector3 getMax();
	void set(Box box);
	void setMinMax(SpacedVector3 min, SpacedVector3 max);
	void setCenterSize(SpacedVector3 center, SpacedVector3 size);
	boolean overlap(Box box);
	void expand(SpacedVector3 vector);
	void translate(SpacedVector3 vector);
}