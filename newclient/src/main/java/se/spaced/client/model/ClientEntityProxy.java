package se.spaced.client.model;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.Entity;

public class ClientEntityProxy implements Entity {
	private final UUID pk;

	public ClientEntityProxy(UUID pk) {
		this.pk = pk;
	}

	@Override
	public UUID getPk() {
		return pk;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Entity)) {
			return false;
		}
		Entity e = (Entity) o;
		UUID pk1 = getPk();
		UUID pk2 = e.getPk();
		if (pk1 != null && pk2 != null) {
			return pk1.equals(pk2);
		}
		return false;
	}

	@Override
	public int hashCode() {
		UUID pk = getPk();
		if (pk != null) {
			return getPk().hashCode();
		}
		return super.hashCode();
	}

	@Override
	public String toString() {
		return "ClientEntityProxy{" +
				"pk=" + pk +
				'}';
	}
}