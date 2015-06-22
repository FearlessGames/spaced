package se.spaced.server.persistence;

public class DuplicateObjectException extends RuntimeException {

	public DuplicateObjectException(String message) {
		super(message);
	}
}
