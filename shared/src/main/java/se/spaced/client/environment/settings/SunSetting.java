package se.spaced.client.environment.settings;

import com.ardor3d.math.ColorRGBA;

public class SunSetting implements Interpolable<SunSetting> {

	private ColorRGBA diffuseColor;
	private ColorRGBA ambientColor;
	private ColorRGBA emissiveColor;

	public SunSetting(ColorRGBA diffuseColor, ColorRGBA ambientColor, ColorRGBA emissiveColor) {
		this.diffuseColor = diffuseColor;
		this.ambientColor = ambientColor;
		this.emissiveColor = emissiveColor;
	}

	public ColorRGBA getDiffuseColor() {
		return diffuseColor;
	}

	public ColorRGBA getAmbientColor() {
		return ambientColor;
	}

	public ColorRGBA getEmissiveColor() {
		return emissiveColor;
	}

	@Override
	public SunSetting interpolate(SunSetting other, float pos) {
		ColorRGBA newAmbientColor = new ColorRGBA();
		ColorRGBA newEmissiveColor = new ColorRGBA();
		ColorRGBA newDiffuseColor = new ColorRGBA();
		ambientColor.lerp(other.getAmbientColor(), pos, newAmbientColor);
		diffuseColor.lerp(other.getDiffuseColor(), pos, newDiffuseColor);
		emissiveColor.lerp(other.getEmissiveColor(), pos, newEmissiveColor);
		return new SunSetting(newDiffuseColor, newAmbientColor, newEmissiveColor);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SunSetting)) {
			return false;
		}

		SunSetting that = (SunSetting) o;

		if (!ambientColor.equals(that.ambientColor)) {
			return false;
		}
		if (!diffuseColor.equals(that.diffuseColor)) {
			return false;
		}
		return emissiveColor.equals(that.emissiveColor);
	}

	@Override
	public int hashCode() {
		int result = diffuseColor.hashCode();
		result = 31 * result + ambientColor.hashCode();
		result = 31 * result + emissiveColor.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "SunSetting{" +
				"diffuseColor=" + diffuseColor +
				", ambientColor=" + ambientColor +
				", emissiveColor=" + emissiveColor +
				'}';
	}
}
