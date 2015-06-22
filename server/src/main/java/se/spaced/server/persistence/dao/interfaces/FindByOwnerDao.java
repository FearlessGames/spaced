package se.spaced.server.persistence.dao.interfaces;

public interface FindByOwnerDao<T extends Persistable> extends Dao<T> {
	T findByOwner(Persistable owner);
}
