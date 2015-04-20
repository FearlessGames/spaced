package se.spaced.client.model.player;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.Relation;

public class TargetInfo {
	private final UUID uuid;
	private final Relation relation;
	private final boolean alive;
	private final ClientEntity clientEntity;
	private final boolean lootable;

	public TargetInfo(UUID uuid, Relation relation, boolean lootable, boolean alive) {
		this(uuid, relation, lootable, alive, null);
	}

	public TargetInfo(UUID uuid, Relation relation, boolean lootable, boolean alive, ClientEntity clientEntity) {
		this.uuid = uuid;
		this.relation = relation;
		this.lootable = lootable;
		this.alive = alive;
		this.clientEntity = clientEntity;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Relation getRelation() {
		return relation;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isLootable() {
		return lootable;
	}

	@Override
	public String toString() {
		return "TargetInfo{" +
				"uuid=" + uuid +
				", relation=" + relation +
				", alive=" + alive +
				", lootable=" + lootable +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TargetInfo that = (TargetInfo) o;

		if (alive != that.alive) {
			return false;
		}
		if (lootable != that.lootable) {
			return false;
		}
		if (relation != that.relation) {
			return false;
		}

		if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = uuid != null ? uuid.hashCode() : 0;
		result = 31 * result + (relation != null ? relation.hashCode() : 0);
		result = 31 * result + (alive ? 1 : 0);
		result = 31 * result + (lootable ? 1 : 0);
		return result;
	}


	public ClientEntity getClientEntity() {
		return clientEntity;
	}
}
