package se.spaced.server.persistence.dao.interfaces;

import se.fearlessgames.common.util.uuid.UUID;

/**
 * Everything that will end up in the database at some point must
 * implement this.
 */
public interface Persistable {
	/**
	 * Gets the public key for this Persistable
	 *
	 * @return the public key
	 */
	UUID getPk();

	void setPk(UUID key);

}