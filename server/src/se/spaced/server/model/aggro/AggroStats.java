package se.spaced.server.model.aggro;

import se.spaced.server.model.ServerEntity;

public class AggroStats implements Comparable<AggroStats> {
	private final ServerEntity serverEntity;
	private final int hate;
	private final int creationId;

	public AggroStats(ServerEntity serverEntity, int hate, int creationId) {
		this.serverEntity = serverEntity;
		this.hate = hate;
		this.creationId = creationId;
	}

	public AggroStats(AggroStats old, int newHate) {
		this(old.serverEntity, newHate, old.creationId);
	}

	public int getHate() {
		return hate;
	}

	public ServerEntity getSpacedServerEntity() {
		return serverEntity;
	}

	@Override
	public int compareTo(AggroStats o) {
		int i = o.hate - hate;
		if (i != 0) {
			return i;
		}
		return creationId - o.creationId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AggroStats that = (AggroStats) o;

		if (serverEntity != null ? !serverEntity.equals(that.serverEntity) : that.serverEntity != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return serverEntity != null ? serverEntity.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "AggroStats{" +
				"serverEntity=" + serverEntity +
				", hate=" + hate +
				", creationId=" + creationId +
				'}';
	}
}
