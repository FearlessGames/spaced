package se.spaced.client.environment.settings;

import org.junit.Before;
import org.junit.Test;
import se.mockachino.annotations.*;

import static se.mockachino.Mockachino.*;

public class EnvSettingsTest {
	@Mock
	private SunSetting sunSetting;
	@Mock
	private FogSetting fogSetting;
	@Mock
	private SoundSetting soundSetting;
	private EnvSettings es;

	@Before
	public void setUp() throws Exception {
		setupMocks(this);
		es = new EnvSettingsImpl(sunSetting, fogSetting, soundSetting);
	}

	@Test
	public void testInterpolate() throws Exception {
		EnvSettingsImpl es2 = new EnvSettingsImpl(sunSetting, fogSetting, soundSetting);
		es.interpolate(es2, 1);
		verifyExactly(1).on(sunSetting).interpolate(sunSetting, 1);
		verifyExactly(1).on(fogSetting).interpolate(fogSetting, 1);
	}
}
