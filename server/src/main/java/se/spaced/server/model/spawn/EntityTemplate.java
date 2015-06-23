package se.spaced.server.model.spawn;

import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

import javax.persistence.Entity;

@Entity
public abstract class EntityTemplate extends ExternalPersistableBase implements NamedPersistable {

	protected final String name;

	protected EntityTemplate() {
		this(null, null);
	}

	protected EntityTemplate(UUID pk, String name) {
		super(pk);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public abstract boolean isPersistent();
}
