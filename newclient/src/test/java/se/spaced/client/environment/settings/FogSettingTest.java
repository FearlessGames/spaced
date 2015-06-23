package se.spaced.client.environment.settings;

import com.ardor3d.math.ColorRGBA;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FogSettingTest {
	FogSetting fogSettingOrigin;
	FogSetting fogSettingDestination;
	private static final float EPSILON = 0.000001f;
	private FogSetting fogSettingOriginTwo;

	@Before
	public void setup() {
		fogSettingDestination = new FogSetting(new ColorRGBA(1f, 1f, 1f, 1f), 1f, 1f, 1f);
		fogSettingOrigin = new FogSetting(new ColorRGBA(0f, 0f, 0f, 0f), 0f, 0f, 0f);
		fogSettingOriginTwo = new FogSetting(new ColorRGBA(3f, 3f, 3f, 3f), 3f, 3f, 3f);
	}

	@Test
	public void testFogInterpolationHalfWay() {
		FogSetting interpolated = fogSettingOrigin.interpolate(fogSettingDestination, 0.5f);
		ColorRGBA expectedFogColor = new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f);
		assertEquals(expectedFogColor, interpolated.getColor());
		assertEquals(0.5f, interpolated.getEnd(), EPSILON);
		assertEquals(0.5f, interpolated.getStart(), EPSILON);
		assertEquals(0.5f, interpolated.getDensity(), EPSILON);
	}

	@Test
	public void testFogNoInterpolation() {
		FogSetting interpolated = fogSettingOriginTwo.interpolate(fogSettingDestination, 0);
		assertEquals(fogSettingOriginTwo.getColor(), interpolated.getColor());
		assertEquals(fogSettingOriginTwo.getEnd(), interpolated.getEnd(), EPSILON);
		assertEquals(fogSettingOriginTwo.getStart(), interpolated.getStart(), EPSILON);
		assertEquals(fogSettingOriginTwo.getDensity(), interpolated.getDensity(), EPSILON);
	}

	@Test
	public void testFogTotallyInterpolated() {
		FogSetting interpolated = fogSettingOriginTwo.interpolate(fogSettingDestination, 1);
		assertEquals(fogSettingDestination.getColor(), interpolated.getColor());
		assertEquals(fogSettingDestination.getEnd(), interpolated.getEnd(), EPSILON);
		assertEquals(fogSettingDestination.getStart(), interpolated.getStart(), EPSILON);
		assertEquals(fogSettingDestination.getDensity(), interpolated.getDensity(), EPSILON);
	}
}
