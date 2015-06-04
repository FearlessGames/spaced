package se.spaced.client.environment.time;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("gametime")
public class GameTime implements Comparable<GameTime> {
	private final long gameTime;

	public GameTime(long gameTime) {
		this.gameTime = gameTime;
	}

	@Override
	public int compareTo(GameTime o) {
		return Long.signum(gameTime - o.gameTime);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GameTime gameTime1 = (GameTime) o;
		return gameTime == gameTime1.gameTime;
	}

	@Override
	public int hashCode() {
		return (int) (gameTime ^ (gameTime >>> 32));
	}

	public GameTime subtract(GameTime time) {
		return new GameTime(gameTime - time.gameTime);
	}

	public GameTime add(GameTime time) {
		return new GameTime(gameTime + time.gameTime);
	}

	public long getValue() {
		return gameTime;
	}

	@Override
	public String toString() {
		return String.format("GameTime: %d", gameTime);
	}
}
