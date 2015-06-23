package se.spaced.server.model.currency;

import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class PersistedCurrency extends ExternalPersistableBase implements NamedPersistable {
	public static final PersistedCurrency NONE = new PersistedCurrency(UUID.fromString(
			"f3578cef-d61e-44fe-a057-9f43c250aee4"), "NONE");

	@Column(unique = true)
	private final String name;

	private PersistedCurrency(UUID pk, String name) {
		super(pk);
		this.name = name;
	}

	public PersistedCurrency() {
		this("");
	}

	public PersistedCurrency(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
