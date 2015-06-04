package se.ardortech.math;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("AABox")
public class AABox implements Box {
	@XStreamAsAttribute
	private SpacedVector3 min;
	@XStreamAsAttribute
	private SpacedVector3 max;

	public AABox(SpacedVector3 min, SpacedVector3 max) {
		this.min = min;
		this.max = max;
	}

	public AABox(Box box) {
		this.min = box.getMin();
		this.max = box.getMax();
	}

	public static AABox fromMinMax(SpacedVector3 min, SpacedVector3 max) {
		return new AABox(min, max);
	}

	public static AABox fromCenterSize(SpacedVector3 center, SpacedVector3 size) {
		return new AABox(new SpacedVector3(center.getX() - size.getX() / 2f,
								   center.getY() - size.getY() / 2f,
								   center.getZ() - size.getZ() / 2f),
						new SpacedVector3(center.getX() + size.getX() / 2f,
							   	   center.getY() + size.getY() / 2f,
							   	   center.getZ() + size.getZ() / 2f));
	}

	@Override
	public void set(Box box) {
		this.min = box.getMin();
		this.max = box.getMax();
	}

	@Override
	public SpacedVector3 getMin() {
		return min;
	}

	@Override
	public SpacedVector3 getMax() {
		return max;
	}

	@Override
	public void setMinMax(SpacedVector3 min, SpacedVector3 max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public SpacedVector3 getCenter() {
		return new SpacedVector3(min.getX() + ((max.getX() - min.getX()) / 2.0),
						   min.getY() + ((max.getY() - min.getY()) / 2.0),
						   min.getZ() + ((max.getZ() - min.getZ()) / 2.0));
	}

	@Override
	public SpacedVector3 getSize() {
		return max.subtract(min);
	}

	@Override
	public void setCenterSize(SpacedVector3 center, SpacedVector3 size) {
		min = new SpacedVector3(	center.getX() - size.getX() / 2f,
									center.getY() - size.getY() / 2f,
									center.getZ() - size.getZ() / 2f);
		max = new SpacedVector3(	center.getX() + size.getX() / 2f,
									center.getY() + size.getY() / 2f,
									center.getZ() + size.getZ() / 2f);
	}

	@Override
	public boolean isInside(SpacedVector3 point) {
		return
			point.getX() >= min.getX() &&
			point.getX() <= max.getX() &&
			point.getY() >= min.getY() &&
			point.getY() <= max.getY() &&
			point.getZ() >= min.getZ() &&
			point.getZ() <= max.getZ();
	}

	@Override
	public boolean isInside(SpacedVector3 point, double margin) {
		return
			point.getX() >= min.getX() - margin &&
			point.getX() <= max.getX() + margin &&
			point.getY() >= min.getY() - margin &&
			point.getY() <= max.getY() + margin &&
			point.getZ() >= min.getZ() - margin &&
			point.getZ() <= max.getZ() + margin;
	}

	@Override
	public boolean overlap(Box box) {
		return
			box.getMax().getX() >= min.getX() &&
			box.getMin().getX() <= max.getX() &&
			box.getMax().getY() >= min.getY() &&
			box.getMin().getY() <= max.getY() &&
			box.getMax().getZ() >= min.getZ() &&
			box.getMin().getZ() <= max.getZ();
	}

	@Override
	public void expand(SpacedVector3 vector) {
		min = new SpacedVector3(	Math.min(min.getX(), vector.getX()),
									Math.min(min.getY(), vector.getY()),
									Math.min(min.getZ(), vector.getZ()));

		max = new SpacedVector3(	Math.max(max.getX(), vector.getX()),
									Math.max(max.getY(), vector.getY()),
									Math.max(max.getZ(), vector.getZ()));
	}

	@Override
	public void translate(SpacedVector3 vector) {
		min = min.add(vector);
		max = max.add(vector);
	}

	@Override
	public Spatial getDebugShape() {
		return new com.ardor3d.scenegraph.shape.Box("", new Vector3(min.getX(), min.getY(), min.getZ()), new Vector3(max.getX(), max.getY(), max.getZ()) );
	}

	@Override
	public double distanceToEdge(SpacedVector3 point) {
		// TODO: implement properly, currently it's always inside
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AABox aaBox = (AABox) o;

		if (!max.equals(aaBox.max)) {
			return false;
		}
		if (!min.equals(aaBox.min)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = min.hashCode();
		result = 31 * result + max.hashCode();
		return result;
	}
}
