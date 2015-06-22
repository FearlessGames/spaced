package se.spaced.server.tools.spawnpattern.presenter;

public class GeometryException extends Exception {
	public GeometryException(String message, Exception e) {
		super(message, e);
	}

	public GeometryException(String s) {
		super(s);
	}
}
