package se.spaced.server.persistence.dao.interfaces;

public interface FindableDao<T extends NamedPersistable> extends Dao<T> {
	T findByName(String name);
}
