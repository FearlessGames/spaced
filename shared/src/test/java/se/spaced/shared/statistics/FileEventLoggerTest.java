package se.spaced.shared.statistics;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.json.GsonSerializer;
import se.fearless.common.time.MockTimeProvider;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FileEventLoggerTest {

	private FileEventLogger fileEventLogger;
	private TestLogger logger;
	private MockTimeProvider timeProvider;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		fileEventLogger = new FileEventLogger(new GsonSerializer(), timeProvider);
		logger = TestLoggerFactory.getTestLogger("EVENT_LOGGER");
		logger.clearAll();
	}

	@Test
	public void logSimpleString() throws Exception {
		fileEventLogger.log("foo");

		ImmutableList<LoggingEvent> allLoggingEvents = logger.getAllLoggingEvents();
		assertEquals(1, allLoggingEvents.size());
		LoggingEvent loggingEvent = allLoggingEvents.get(0);

		assertEquals("{0,\"foo\"}", loggingEvent.getMessage());
	}

	@Test
	public void logMultipleStrings() throws Exception {
		timeProvider.advanceTime(145);
		fileEventLogger.log("one", "two", "three", 4);

		ImmutableList<LoggingEvent> allLoggingEvents = logger.getAllLoggingEvents();
		assertEquals(1, allLoggingEvents.size());
		LoggingEvent loggingEvent = allLoggingEvents.get(0);

		assertEquals("{145,\"one\",\"two\",\"three\",4}", loggingEvent.getMessage());
	}

	@Test
	public void logList() throws Exception {
		fileEventLogger.log(Arrays.asList("one", "two", "three", 4));

		ImmutableList<LoggingEvent> allLoggingEvents = logger.getAllLoggingEvents();
		assertEquals(1, allLoggingEvents.size());
		LoggingEvent loggingEvent = allLoggingEvents.get(0);

		assertEquals("{0,[\"one\",\"two\",\"three\",4]}", loggingEvent.getMessage());

	}
}