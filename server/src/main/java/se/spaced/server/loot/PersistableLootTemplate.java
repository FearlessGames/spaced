package se.spaced.server.loot;

import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

import javax.persistence.Entity;

@Entity
public abstract class PersistableLootTemplate extends ExternalPersistableBase implements LootTemplate, NamedPersistable {

	private String name;

	protected PersistableLootTemplate() {
	}

	protected PersistableLootTemplate(UUID pk, String name) {
		super(pk);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (name != null) {
			builder.append(name);
		}
		builder.append(" - ");
		builder.append(getPk());
		builder.append(" - ");
		builder.append(getClass().getSimpleName());
		return builder.toString();
	}
}
