package se.fearless.bender;

import org.junit.Before;
import org.junit.Test;
import se.fearless.bender.statistics.MessageStatisticsService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageStatisticsServiceTest {
	private MessageStatisticsService messageStatisticsService;

	@Before
	public void testCreate() {
		messageStatisticsService = new MessageStatisticsService();
	}

	@Test
	public void testAdd() {
		String user = "bronzon";
		String message = "I rule";
		messageStatisticsService.add(user, message);
		messageStatisticsService.add(user, message);
		assertEquals("bronzon has sent 2 message(s)", messageStatisticsService.getMessageStatisticsForUser("bronzon"));
	}

	@Test
	public void testGettingStatisticsRundown() {
		String user = "bronzon";
		String message = "I rule";
		messageStatisticsService.add(user, message);
		user = "thecookieDough";
		messageStatisticsService.add(user, message);
		String messageStatisticOverview = messageStatisticsService.getMessageStatisticOverview();
		assertTrue(messageStatisticOverview.contains("thecookieDough:1(50.0%)"));
		assertTrue(messageStatisticOverview.contains("bronzon:1(50.0%)"));
	}

}
