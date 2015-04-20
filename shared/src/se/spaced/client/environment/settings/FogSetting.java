package se.spaced.client.environment.settings;

import com.ardor3d.math.ColorRGBA;

public class FogSetting implements Interpolable<FogSetting> {
	private ColorRGBA color;
	private float end;
	private float density;
	private float start;

	@Override
	public FogSetting interpolate(FogSetting other, float pos) {
		ColorRGBA newColor = new ColorRGBA();
		newColor = color.lerp(other.getColor(), pos, newColor);
		float newEnd = (1 - pos) * end + pos * other.end;
		float newDensity = (1 - pos) * density + pos * other.density;
		float newStart = (1 - pos) * start + pos * other.start;
		return new FogSetting(newColor, newEnd, newDensity, newStart);
	}

	public FogSetting(ColorRGBA color, float end, float density, float start) {
		this.color = color;
		this.end = end;
		this.density = density;
		this.start = start;
	}

	public ColorRGBA getColor() {
		return color;
	}

	public float getEnd() {
		return end;
	}

	public float getDensity() {
		return density;
	}

	public float getStart() {
		return start;
	}

	public void setColor(ColorRGBA color) {
		this.color = color;
	}

	public void setEnd(float end) {
		this.end = end;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public void setStart(float start) {
		this.start = start;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FogSetting that = (FogSetting) o;

		if (Float.compare(that.density, density) != 0) {
			return false;
		}
		if (Float.compare(that.end, end) != 0) {
			return false;
		}
		if (Float.compare(that.start, start) != 0) {
			return false;
		}
		if (!color.equals(that.color)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = color.hashCode();
		result = 31 * result + (end != +0.0f ? Float.floatToIntBits(end) : 0);
		result = 31 * result + (density != +0.0f ? Float.floatToIntBits(density) : 0);
		result = 31 * result + (start != +0.0f ? Float.floatToIntBits(start) : 0);
		return result;
	}

	@Override
	public String toString() {
		return "FogSetting{" +
				"color=" + color +
				", end=" + end +
				", density=" + density +
				", start=" + start +
				'}';
	}
}