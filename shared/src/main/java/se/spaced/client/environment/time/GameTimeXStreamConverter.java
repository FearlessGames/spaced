package se.spaced.client.environment.time;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.converters.SingleValueConverter;

@Singleton
public class GameTimeXStreamConverter implements SingleValueConverter {
	private final GameTimeManager gameTimeManager;

	@Inject
	public GameTimeXStreamConverter(GameTimeManager gameTimeManager) {
		this.gameTimeManager = gameTimeManager;
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(GameTime.class);
	}

	@Override
	public String toString(Object obj) {
		return GameTimeParser.toString((GameTime) obj, gameTimeManager.getTimeInfo());
	}

	@Override
	public Object fromString(String str) {
		return GameTimeParser.parse(str, gameTimeManager.getTimeInfo());
	}
}
