package se.spaced.shared.xml;

public class XmlIOException extends Exception {
	public XmlIOException(Exception e) {
		super(e);
	}

	public XmlIOException(String message, Exception e) {
		super(message, e);
	}
}
