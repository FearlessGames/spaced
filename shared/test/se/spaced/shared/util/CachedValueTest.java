package se.spaced.shared.util;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;

import static org.junit.Assert.assertTrue;

public class CachedValueTest implements CacheUpdater<String> {

	private MockTimeProvider timeProvider;

	@Before
	public void setUp() {
		timeProvider = new MockTimeProvider();
		timeProvider.setNow(1);
	}

	@Test
	public void testCacheExpired() {
		CachedValue<String> cv = new CachedValue<String>(timeProvider, TimeConverter.ONE_MINUTE.getTimeInMillis(), this);
		cv.setCachedData("1");
		assertTrue(cv.getCachedData().equals("1"));
		timeProvider.advanceTime(TimeConverter.THIRTY_MINUTES.getTimeInMillis());
		assertTrue(cv.getCachedData().equals("2"));

	}

	@Override
	public String refreshCashedData() {
		return "2";
	}
}
