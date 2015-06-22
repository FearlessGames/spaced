package se.spaced.server.persistence;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.dao.interfaces.Persistable;

public class MockOwner implements Persistable {
	private final UUID pk;

	public MockOwner(UUID pk) {
		this.pk = pk;
	}

	@Override
	public UUID getPk() {
		return pk;
	}

	@Override
	public void setPk(UUID key) {
	}
}
