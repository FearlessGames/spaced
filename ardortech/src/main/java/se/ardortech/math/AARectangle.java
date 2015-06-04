package se.ardortech.math;

import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyVector2;

public class AARectangle implements Rectangle {
	private Vector2 min;
	private Vector2 max;

	public AARectangle(ReadOnlyVector2 min, ReadOnlyVector2 max) {
		this.min = new Vector2(min);
		this.max = new Vector2(max);
	}

	public AARectangle(AARectangle rectangle) {
		this.min = new Vector2(rectangle.getMin());
		this.max = new Vector2(rectangle.getMax());
	}

	public static AARectangle fromMinMax(ReadOnlyVector2 min, ReadOnlyVector2 max) {
		return new AARectangle(min, max);
	}

	public static AARectangle fromCenterSize(ReadOnlyVector2 center, ReadOnlyVector2 size) {
		return new AARectangle(new Vector2(center.getX() - size.getX() / 2f,
											   center.getY() - size.getY() / 2f),
								   new Vector2(center.getX() + size.getX() / 2f,
										   	   center.getY() + size.getY() / 2f));
	}

	@Override
	public void set(Rectangle rectangle) {
		this.min = new Vector2(rectangle.getMin());
		this.max = new Vector2(rectangle.getMax());
	}

	@Override
	public ReadOnlyVector2 getMin() {
		return new Vector2(min);
	}

	@Override
	public ReadOnlyVector2 getMax() {
		return new Vector2(max);
	}

	@Override
	public void setMinMax(ReadOnlyVector2 min, ReadOnlyVector2 max) {
		this.min = new Vector2(min);
		this.max = new Vector2(max);
	}

	@Override
	public ReadOnlyVector2 getCenter() {
		return new Vector2(min.getX() + ((max.getX() - min.getX()) / 2.0),
						   min.getY() + ((max.getY() - min.getY()) / 2.0));
	}

	@Override
	public ReadOnlyVector2 getSize() {
		return new Vector2(max.getX() - min.getX(), max.getY() - min.getY());
	}

	@Override
	public void setCenterSize(ReadOnlyVector2 center, ReadOnlyVector2 size) {
		min.setX(center.getX() - size.getX() / 2f);
		min.setY(center.getY() - size.getY() / 2f);
		max.setX(center.getX() + size.getX() / 2f);
		max.setY(center.getY() + size.getY() / 2f);
	}

	@Override
	public boolean isInside(ReadOnlyVector2 point) {
		return
			point.getX() >= min.getX() &&
			point.getX() <= max.getX() &&
			point.getY() >= min.getY() &&
			point.getY() <= max.getY();
	}

	@Override
	public boolean overlap(Rectangle rectangle) {
		return
			rectangle.getMax().getX() >= min.getX() &&
			rectangle.getMin().getX() <= max.getX() &&
			rectangle.getMax().getY() >= min.getY() &&
			rectangle.getMin().getY() <= max.getY();
	}

	@Override
	public void expand(ReadOnlyVector2 vector) {
		if (min.getX() > vector.getX()) {
			min.setX(vector.getX());
		}

		if (min.getY() > vector.getY()) {
			min.setY(vector.getY());
		}

		if (max.getX() < vector.getX()) {
			max.setX(vector.getX());
		}

		if (max.getY() < vector.getY()) {
			max.setY(vector.getY());
		}
	}

	@Override
	public void translate(ReadOnlyVector2 vector) {
		min.addLocal(vector);
		max.addLocal(vector);
	}
}