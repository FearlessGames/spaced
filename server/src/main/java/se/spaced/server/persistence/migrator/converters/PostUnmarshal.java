package se.spaced.server.persistence.migrator.converters;

public interface PostUnmarshal {
	void postUnmarshal(Object unmarshaledObject);
}
