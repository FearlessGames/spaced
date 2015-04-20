package se.spaced.server.persistence.dao.impl.inmemory;

import se.spaced.server.persistence.dao.impl.OwnedPersistableBase;
import se.spaced.server.persistence.dao.interfaces.FindByOwnerDao;
import se.spaced.server.persistence.dao.interfaces.Persistable;

public class OwnedInMemoryDao<T extends OwnedPersistableBase> extends InMemoryDao<T> implements FindByOwnerDao<T> {

	@Override
	public T findByOwner(Persistable owner) {
		return super.findByPk(owner.getPk());
	}
}
