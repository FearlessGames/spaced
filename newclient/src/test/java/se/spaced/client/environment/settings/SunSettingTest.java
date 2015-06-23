package se.spaced.client.environment.settings;

import com.ardor3d.math.ColorRGBA;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SunSettingTest {
	SunSetting sunSettingOrigin;
	SunSetting sunSettingDestination;
	private static final float EPSILON = 0.000001f;

	@Before
	public void setup() {
		sunSettingDestination = new SunSetting(new ColorRGBA(1f, 1f, 1f, 1f),
				new ColorRGBA(1f, 1f, 1f, 1f),
				new ColorRGBA(1f, 1f, 1f, 1f));
		sunSettingOrigin = new SunSetting(new ColorRGBA(0f, 0f, 0f, 0f),
				new ColorRGBA(0f, 0f, 0f, 0f),
				new ColorRGBA(0f, 0f, 0f, 0f));
	}

	@Test
	public void testSunInterpolationHalfWay() {
		SunSetting interpolated = sunSettingOrigin.interpolate(sunSettingDestination, 0.5f);
		ColorRGBA expectedColor = new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f);
		assertEquals(expectedColor, interpolated.getDiffuseColor());
		assertEquals(expectedColor, interpolated.getAmbientColor());
		assertEquals(expectedColor, interpolated.getEmissiveColor());
	}

	@Test
	public void testSunInterpolationZero() {
		SunSetting interpolated = sunSettingOrigin.interpolate(sunSettingDestination, 0f);
		assertEquals(sunSettingOrigin.getDiffuseColor(), interpolated.getDiffuseColor());
		assertEquals(sunSettingOrigin.getAmbientColor(), interpolated.getAmbientColor());
		assertEquals(sunSettingOrigin.getEmissiveColor(), interpolated.getEmissiveColor());
	}

	@Test
	public void testSunInterpolationOne() {
		SunSetting interpolated = sunSettingOrigin.interpolate(sunSettingDestination, 1f);
		assertEquals(sunSettingDestination.getDiffuseColor(), interpolated.getDiffuseColor());
		assertEquals(sunSettingDestination.getAmbientColor(), interpolated.getAmbientColor());
		assertEquals(sunSettingDestination.getEmissiveColor(), interpolated.getEmissiveColor());
	}
}
