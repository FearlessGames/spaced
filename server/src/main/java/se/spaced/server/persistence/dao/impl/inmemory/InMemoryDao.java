package se.spaced.server.persistence.dao.impl.inmemory;

import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDMockFactory;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.impl.OwnedPersistableBase;
import se.spaced.server.persistence.dao.interfaces.Dao;
import se.spaced.server.persistence.dao.interfaces.Persistable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDao<T extends Persistable> implements Dao<T> {
	protected final ConcurrentHashMap<UUID, T> data = new ConcurrentHashMap<UUID, T>();

	private final UUIDFactory uuidFactory;

	public InMemoryDao() {
		this(new UUIDMockFactory());
	}

	public InMemoryDao(UUIDFactory uuidFactory) {
		this.uuidFactory = uuidFactory;
	}

	@Override
	public T persist(T obj) {
		UUID pk = obj.getPk();
		if (pk == null) {
			if (!(obj instanceof ExternalPersistableBase)) {
				pk = uuidFactory.combUUID();
				obj.setPk(pk);
			} else {
				throw new IllegalArgumentException("ExternalPersistableBase must have pk declared " + obj);
			}
		}
		if (obj instanceof OwnedPersistableBase) {
			pk = ((OwnedPersistableBase) obj).getOwnerPk();
		}
		T old = findByPk(pk);
		if (old == null) {
			data.put(pk, obj);
		} else if (old != obj) {
			throw new DuplicateObjectException("Trying to persist object " + obj + " with same pk as old object " + old);
		}
		return obj;
	}

	@Override
	public void delete(T obj) {
		if (obj != null) {
			for (Map.Entry<UUID, T> uuidtEntry : data.entrySet()) {
				if (uuidtEntry.getValue() == obj) {
					data.remove(uuidtEntry.getKey());
					return;
				}
			}
		}
	}

	@Override
	public T findByPk(UUID key) {
		return data.get(key);
	}

	@Override
	public List<T> findAll() {
		return new ArrayList<T>(data.values());
	}

	@Override
	public void deleteAll() {
		data.clear();
	}
}
