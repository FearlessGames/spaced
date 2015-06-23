package se.spaced.server.model.spawn;

import se.fearless.common.uuid.UUID;

import javax.persistence.Entity;

@Entity
public class PlayerTemplate extends EntityTemplate {

	protected PlayerTemplate() {
		super(null, null);
	}

	public PlayerTemplate(UUID pk, String name) {
		super(pk, name);
	}

	@Override
	public boolean isPersistent() {
		return true;
	}

	@Override
	public String toString() {
		return "PlayerTemplate{" +
				"name='" + name + '\'' +
				'}';
	}
}
