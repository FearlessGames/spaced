package se.spaced.shared.world.terrain;

public class HeightMapLoadException extends RuntimeException {
	public HeightMapLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public HeightMapLoadException(String message) {
		super(message);
	}
}
