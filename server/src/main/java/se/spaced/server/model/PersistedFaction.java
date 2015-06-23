package se.spaced.server.model;

import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class PersistedFaction extends ExternalPersistableBase implements NamedPersistable {
	@Column(unique = true)
	private String name;

	protected PersistedFaction() {
		this(null, null);
	}

	public PersistedFaction(UUID pk, String name) {
		super(pk);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "PersistedFaction{" +
				"name='" + name + '\'' +
				'}';
	}
}