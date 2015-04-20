package se.spaced.client.environment.time;

import org.junit.Before;
import org.junit.Test;
import se.spaced.shared.world.TimeSystemInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameTimeXStreamConverterTest {
	private GameTimeManager gameTimeManager;
	private GameTimeXStreamConverter gameTimeXStreamConverter;

	@Before
	public void setUp() {
		gameTimeManager = new HourMinuteGameTimeManager(new TimeSystemInfo(10, 10, 10, 1.0));
		gameTimeXStreamConverter = new GameTimeXStreamConverter(gameTimeManager);
	}

	@Test
	public void testCanConvert() throws Exception {
		assertTrue(gameTimeXStreamConverter.canConvert(GameTime.class));
	}

	@Test
	public void testToString() throws Exception {
		GameTime gameTime = new GameTime(0L);
		assertEquals("00:00", gameTimeXStreamConverter.toString(gameTime));
	}

	@Test
	public void testFromString() throws Exception {
		assertEquals(new GameTime(0L), gameTimeXStreamConverter.fromString("00:00"));
	}
}
