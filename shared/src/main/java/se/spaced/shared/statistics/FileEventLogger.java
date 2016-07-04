package se.spaced.shared.statistics;

import com.google.inject.Inject;
import org.slf4j.Logger;
import se.fearless.common.json.JsonSerializer;
import se.fearless.common.time.TimeProvider;

import java.util.StringJoiner;

import static org.slf4j.LoggerFactory.getLogger;

public class FileEventLogger implements EventLogger {
	private final Logger logger = getLogger("EVENT_LOGGER");
	private final JsonSerializer jsonSerializer;
	private final TimeProvider timeProvider;

	@Inject
	public FileEventLogger(JsonSerializer jsonSerializer, TimeProvider timeProvider) {
		this.jsonSerializer = jsonSerializer;
		this.timeProvider = timeProvider;
	}

	@Override
	public void log(Object first, Object... objects) {
		long timeStamp = timeProvider.now();
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add(Long.toString(timeStamp));
		joiner.add(jsonSerializer.toJson(first));
		for (Object object : objects) {
			joiner.add(jsonSerializer.toJson(object));
		}
		logger.info(joiner.toString());
	}

}
