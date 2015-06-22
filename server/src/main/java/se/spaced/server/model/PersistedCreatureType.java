package se.spaced.server.model;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

import javax.persistence.Entity;

@Entity
public class PersistedCreatureType extends ExternalPersistableBase implements NamedPersistable {
	private final String name;

	protected PersistedCreatureType() {
		this(null, null);
	}

	public PersistedCreatureType(UUID pk, String name) {
		super(pk);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return "PersistedCreatureType{" +
				"name='" + name + '\'' +
				'}';
	}
}
