package se.spaced.server.persistence;

public class ObjectNotFoundException extends Exception {
	private static final long serialVersionUID = 211976339921654294L;

	public ObjectNotFoundException(String message) {
		super(message);
	}
}
