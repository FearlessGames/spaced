package se.spaced.server.model.world;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.spaced.shared.world.TimeSystemInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TimeServiceTest {

	private MockTimeProvider timeProvider;
	private TimeService timeService;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		timeService = new TimeServiceImpl(timeProvider);
	}

	@Test
	public void registerWorld() throws Exception {
		TimeSystemInfo timeSystemInfo = new TimeSystemInfo(24, 60, 60, 1.0);
		timeService.registerZone("foo", timeSystemInfo);

		TimeSystemInfo info = timeService.getDayInfo("foo");
		assertNotNull(info);
		assertEquals(new TimeSystemInfo(24, 60, 60, 1.0), info);
	}

	@Test(expected = RuntimeException.class)
	public void unknownWorld() throws Exception {
		TimeSystemInfo info = timeService.getDayInfo("foo");
	}

	@Test
	public void getCurrentDayOffsetInit() throws Exception {
		TimeSystemInfo timeSystemInfo = new TimeSystemInfo(10, 10, 10, 1.0);
		timeService.registerZone("zone", timeSystemInfo);

		assertEquals(0L, timeService.getCurrentDayOffsetInMillis("zone"));
	}

	@Test
	public void getCurrentDayOffsetInitAfter0() throws Exception {
		timeProvider.advanceTime(300);
		TimeSystemInfo timeSystemInfo = new TimeSystemInfo(10, 10, 10, 1.0);
		timeService.registerZone("zone", timeSystemInfo);

		assertEquals(0L, timeService.getCurrentDayOffsetInMillis("zone"));
	}

	@Test
	public void getCurrentDayOffset() throws Exception {
		timeProvider.advanceTime(300);
		TimeSystemInfo timeSystemInfo = new TimeSystemInfo(10, 10, 10, 1.0);
		timeService.registerZone("zone", timeSystemInfo);

		timeProvider.advanceTime(500);

		assertEquals(500L, timeService.getCurrentDayOffsetInMillis("zone"));
	}

	@Test
	public void getCurrentDayOffsetMovePastMidnight() throws Exception {
		timeProvider.advanceTime(1000);
		TimeSystemInfo timeSystemInfo = new TimeSystemInfo(10, 10, 10, 1.0);
		timeService.registerZone("zone", timeSystemInfo);

		timeProvider.advanceTime(999 * 1000);
		assertEquals(999 * 1000, timeService.getCurrentDayOffsetInMillis("zone"));
		timeProvider.advanceTime(1000);
		assertEquals(0L, timeService.getCurrentDayOffsetInMillis("zone"));
		timeProvider.advanceTime(1000);
		assertEquals(1000L, timeService.getCurrentDayOffsetInMillis("zone"));
	}


	@Test(expected = RuntimeException.class)
	public void getCurrentOffsetFromUnknown() throws Exception {
		timeService.getCurrentDayOffsetInMillis("foo");
	}
}
