package se.spaced.server.persistence.dao.interfaces;

import se.fearless.common.uuid.UUID;

import java.util.List;

/**
 * The base interface for all DAOs (data access objects) in spaced
 * Specifies the operations all persistable entities need.
 *
 * @param <T> the type of object to save / fetch from the data source
 */
public interface Dao<T extends Persistable> {
	/**
	 * Saves an object to the storage
	 * Postcondition: findByPk with the key returned from this method will return
	 * an object for which obj.equals() will return true
	 *
	 * @param obj the object to persist.
	 * @return the generated public key for this object
	 */
	T persist(T obj);

	void delete(T obj);

	T findByPk(UUID key);

	List<T> findAll();

	void deleteAll();
}

