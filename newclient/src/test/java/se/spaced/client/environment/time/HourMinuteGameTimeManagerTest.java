package se.spaced.client.environment.time;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.util.MockTimeProvider;
import se.spaced.shared.world.TimeSystemInfo;

import static org.junit.Assert.assertEquals;

public class HourMinuteGameTimeManagerTest {
	private HourMinuteGameTimeManager hourMinuteGameTimeManager;
	public static final int MINUTES_PER_HOUR = 10;
	private static final int HOURS_PER_DAY = 10;
	private static final int SECONDS_PER_MINUTE = 60;

	@Before
	public void setUp() {
		hourMinuteGameTimeManager = new HourMinuteGameTimeManager(new TimeSystemInfo(HOURS_PER_DAY, MINUTES_PER_HOUR, 60, 1.0));
	}

	public static void main(String[] args) {
		HourMinuteGameTimeManager manager = new HourMinuteGameTimeManager(new TimeSystemInfo(24, 60, 60, 1.0/48));
		MockTimeProvider time = new MockTimeProvider();
		for (int i = 0; i < 60; i++) {
			time.advanceTime(1000 * 60);
			GameTime gameTime = manager.fromSystemTime(time.now());
			System.out.println(GameTimeParser.toString(gameTime, manager.getTimeInfo()) + " " + manager.getDayFraction(gameTime));
		}
	}

	@Test
	public void testParsingTimeFromString() {
		GameTime gametime = GameTimeParser.parse("01:01", hourMinuteGameTimeManager.getTimeInfo()); //hour 10, minute 10
		assertEquals(11 * SECONDS_PER_MINUTE * 1000, gametime.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParsingBounderiesOneMinuteAbove() {
		GameTimeParser.parse("09:10", hourMinuteGameTimeManager.getTimeInfo());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParsingBounderiesOneHourAbove() {
		GameTimeParser.parse("10:09", hourMinuteGameTimeManager.getTimeInfo());
	}

	@Test
	public void testParsingBounderiesOnBoundery() {
		GameTimeParser.parse("09:09", hourMinuteGameTimeManager.getTimeInfo());
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseWithOnlyDelimiter() throws Exception {
		GameTimeParser.parse(":", hourMinuteGameTimeManager.getTimeInfo());
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseWithNoDelimiter() throws Exception {
		GameTimeParser.parse("0930", hourMinuteGameTimeManager.getTimeInfo());
	}

	@Test
	public void timeToString() throws Exception {
		assertEquals("04:50", GameTimeParser.toString(new GameTime(17420L), new TimeSystemInfo(24, 60, 60, 0.001)));
	}

	@Test
	public void testGettingStringFromGameTime() {
		assertEquals("01:01", GameTimeParser.toString(new GameTime(11 * SECONDS_PER_MINUTE * 1000), hourMinuteGameTimeManager.getTimeInfo()));
	}

	@Test
	public void testGetTimeFromSystemTime() {
		assertEquals(1L, hourMinuteGameTimeManager.fromSystemTime(6000001L).getValue()); //cycle time + 1
	}

	@Test
	public void getCycleTime() throws Exception {
		GameTime cycleTime = hourMinuteGameTimeManager.getCycleTime();
		assertEquals(SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY * 1000, cycleTime.getValue());
	}

	@Test
	public void simplSetTimeInfo() throws Exception {
		MockTimeProvider time = new MockTimeProvider();
		time.setNow(1234567L);
		hourMinuteGameTimeManager.setLocalTimeInfo(new TimeSystemInfo(10, 10, 10, 1.0), 12 * 1000, time.now());
		assertEquals(new GameTime(12 * 1000), hourMinuteGameTimeManager.fromSystemTime(time.now()));

		time.advanceTime(300);
		assertEquals(new GameTime(12 * 1000 + 300), hourMinuteGameTimeManager.fromSystemTime(time.now()));
	}

	@Test
	public void setTimeInfoAndMovePastMidnight() throws Exception {
		MockTimeProvider time = new MockTimeProvider();
		time.setNow(7564512L);
		hourMinuteGameTimeManager.setLocalTimeInfo(new TimeSystemInfo(10, 10, 10, 1.0), 12 * 1000, time.now());

		time.advanceTime(10 * 10 * 10 * 1000 + 300);
		assertEquals(new GameTime(12 * 1000 + 300), hourMinuteGameTimeManager.fromSystemTime(time.now()));
	}

	@Test
	public void setTimeInfoShorterDay() throws Exception {
		MockTimeProvider time = new MockTimeProvider();
		time.setNow(7564512L);
		TimeSystemInfo timeSystemInfo = new TimeSystemInfo(10, 10, 10, 0.25);
		hourMinuteGameTimeManager.setLocalTimeInfo(timeSystemInfo, 20 * 1000, time.now());
		assertEquals(GameTimeParser.parse("00:08", timeSystemInfo), hourMinuteGameTimeManager.fromSystemTime(time.now()));

		time.advanceTime(245000);
		assertEquals(new GameTime(15000), hourMinuteGameTimeManager.fromSystemTime(time.now()));

		time.advanceTime(5000);
		assertEquals(new GameTime(20000), hourMinuteGameTimeManager.fromSystemTime(time.now()));
	}

}
