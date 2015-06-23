package se.spaced.client.environment.settings;

import com.ardor3d.math.ColorRGBA;
import org.junit.Before;
import org.junit.Test;
import se.spaced.client.environment.time.GameTime;

import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class KeyFrameSettingsInterpolatorTest {
	ColorRGBA white = new ColorRGBA(1, 1, 1, 1);
	ColorRGBA gray = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);
	ColorRGBA black = new ColorRGBA(0, 0, 0, 1);
	private KeyFrameSettingsInterpolator<SunSetting> interpolator;
	private SunSetting whiteSetting = new SunSetting(white, white, white);
	private SunSetting blackSetting = new SunSetting(black, black, black);
	private SunSetting graySetting = new SunSetting(gray, gray, gray);

	@Before
	public void setup() {
		GameTime cycleTime = new GameTime(10);
		NavigableMap<GameTime, SunSetting> keyframes = new TreeMap<GameTime, SunSetting>();
		keyframes.put(new GameTime(1), whiteSetting);
		keyframes.put(new GameTime(3), blackSetting);
		interpolator = new KeyFrameSettingsInterpolator<SunSetting>("sun", cycleTime, keyframes);
	}

	@Test
	public void testSimple1() {
		SunSetting setting = interpolator.getSettings(new GameTime(1));
		assertEquals(whiteSetting, setting);
	}

	@Test
	public void testSimple2() {
		SunSetting setting = interpolator.getSettings(new GameTime(3));
		assertEquals(blackSetting, setting);
	}

	@Test
	public void testInterpolated() {
		SunSetting setting = interpolator.getSettings(new GameTime(2));
		assertEquals(graySetting, setting);
	}


	@Test
	public void testInterpolatedWrapping() {
		SunSetting setting = interpolator.getSettings(new GameTime(7));
		assertEquals(graySetting, setting);
	}

}
