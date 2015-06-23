package se.spaced.server.persistence.dao.impl.inmemory;

import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.spaced.server.persistence.dao.interfaces.FindableDao;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

import java.util.Map;

public class FindableInMemoryDao<T extends NamedPersistable> extends InMemoryDao<T> implements FindableDao<T> {

	public FindableInMemoryDao() {
	}

	public FindableInMemoryDao(UUIDFactory uuidFactory) {
		super(uuidFactory);
	}

	@Override
	public T findByName(String name) {
		Map<UUID, T> data = this.data;
		for (T t : data.values()) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}
}
