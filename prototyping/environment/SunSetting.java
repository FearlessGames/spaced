import com.ardor3d.math.ColorRGBA;

public class SunSetting {
	private ColorRGBA diffuseColor;
	long occursAt;

	public long getOccursAt() {
		return occursAt;
	}

	public void setOccursAt(long occursAt) {
		this.occursAt = occursAt;
	}

	public SunSetting(ColorRGBA diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	public SunSetting getInterpolatedSetting(long time, long doneBy, SunSetting previous) {
		return new SunSetting(colorInterpolation(time, doneBy, previous.getDiffuseColor()));
	}

	private ColorRGBA colorInterpolation(long time, long doneBy, ColorRGBA diffuseColor) {
		return null; // do stuff.
	}

	public ColorRGBA getDiffuseColor() {
		return diffuseColor;
	}
}
